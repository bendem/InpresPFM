package be.hepl.benbear.accountingclient;

import be.hepl.benbear.bisamap.GetNextBillPacket;
import be.hepl.benbear.bisamap.GetNextBillResponsePacket;
import be.hepl.benbear.bisamap.ListBillsPacket;
import be.hepl.benbear.bisamap.ListBillsResponsePacket;
import be.hepl.benbear.bisamap.ValidateBillPacket;
import be.hepl.benbear.bisamap.ValidateBillResponsePacket;
import be.hepl.benbear.commons.jfx.Inputs;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.security.Cipheriscope;
import be.hepl.benbear.commons.security.Digestion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.crypto.SecretKey;

public class AccountingController implements Initializable {

    private final AccountingApplication app;
    @FXML private Button nextBill;
    @FXML private TextField billField;
    @FXML private Button validateField;
    @FXML private ListView<String> billList;
    @FXML private DatePicker from;
    @FXML private TextField companyId;
    @FXML private DatePicker to;
    @FXML private Button list;
    @FXML private Button sendBills;
    private int billId = -1;

    public AccountingController(AccountingApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nextBill.setOnAction(e -> {
            SecretKey cryptKey = app.getCryptKey();

            Log.d("key: %s, encoded: %s, length: %d, algo: %s, format: %s, destroyed: %s",
                cryptKey, Arrays.toString(cryptKey.getEncoded()), cryptKey.getEncoded().length,
                cryptKey.getAlgorithm(), cryptKey.getFormat(), cryptKey.isDestroyed());

            app.write(new GetNextBillPacket(app.getSession()));
            GetNextBillResponsePacket p = app.readSpecific(GetNextBillResponsePacket.class);
            if(p.getBill() == null) {
                Log.i("No bill available");
                billId = -1;
                return;
            }

            String bill = new String(Cipheriscope.decrypt(cryptKey, p.getBill()));
            billId = Integer.parseInt(bill.split(":")[0]);
            billField.setText(formatBill(bill));
            nextBill.setDisable(true);
            validateField.setDisable(false);
        });

        validateField.setOnAction(e -> {
            if(billId < 0) {
                return;
            }

            byte[] hash = Digestion.digest(ByteBuffer.allocate(4).putInt(billId));
            byte[] signature = Cipheriscope.encrypt(app.getSignKey(), hash);

            app.write(new ValidateBillPacket(app.getSession(), billId, signature));
            ValidateBillResponsePacket p = app.readSpecific(ValidateBillResponsePacket.class);
            if(!p.wasValid()) {
                app.alert(Alert.AlertType.ERROR, "Signature was invalid", this).showAndWait();
            }

            billId = -1;
            billField.setText("");
            nextBill.setDisable(false);
            validateField.setDisable(true);
        });

        list.setOnAction(e -> {
            long from = Optional.ofNullable(this.from.getValue()).orElseGet(LocalDate::now).toEpochDay();
            long to = Optional.ofNullable(this.to.getValue()).orElse(LocalDate.now().plusDays(1)).toEpochDay();
            if(from > to) {
                long tmp = from;
                from = to;
                to = tmp;
            }
            int company = Integer.parseInt(companyId.getText());

            ByteBuffer bytes = ByteBuffer.allocate(20)
                .putInt(company)
                .putLong(from)
                .putLong(to);
            byte[] hash = Digestion.digest(bytes);
            byte[] signature = Cipheriscope.encrypt(app.getSignKey(), hash);

            app.write(new ListBillsPacket(app.getSession(), company, from, to, signature));
            ListBillsResponsePacket answer = app.readSpecific(ListBillsResponsePacket.class);
            if(answer.getBills() == null) {
                app.alert(Alert.AlertType.INFORMATION, "No bills to display", this).showAndWait();
                return;
            }
            String[] bills = new String(Cipheriscope.decrypt(app.getCryptKey(), answer.getBills())).split(";");
            if(bills.length == 1 && bills[0].isEmpty()) {
                bills = new String[0];
            }
            Log.d("Got %d bills", bills.length);
            billList.getItems().setAll(Arrays.stream(bills).map(this::formatBill).toArray(String[]::new));
        });

        from.setValue(LocalDate.now());
        to.setValue(LocalDate.now().plusDays(1));
        Inputs.integer(companyId, 1, Integer.MAX_VALUE);

        sendBills.setOnAction(e -> {
            // TODO
        });
    }

    private String formatBill(String bill) {
        String[] parts = bill.split(":");
        return String.format("id: %s, company id: %s, date: %s, price (no vat): %s, price (vat): %s",
            parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

}
