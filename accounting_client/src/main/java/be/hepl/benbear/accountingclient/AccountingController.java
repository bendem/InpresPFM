package be.hepl.benbear.accountingclient;

import be.hepl.benbear.bisamap.GetNextBillPacket;
import be.hepl.benbear.bisamap.GetNextBillResponsePacket;
import be.hepl.benbear.bisamap.ValidateBillPacket;
import be.hepl.benbear.bisamap.ValidateBillResponsePacket;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.security.Cipheriscope;
import be.hepl.benbear.commons.security.Digestion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.crypto.SecretKey;

public class AccountingController implements Initializable {

    private final AccountingApplication app;
    @FXML private Button nextBill;
    @FXML private TextField billField;
    @FXML private Button validateField;

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
            String bill = new String(Cipheriscope.decrypt(cryptKey, p.getBill()));
            billField.setText(bill);
            nextBill.setDisable(true);
            validateField.setDisable(false);
        });

        validateField.setOnAction(e -> {
            int billId = Integer.parseInt(billField.getText().split(":")[0]);

            byte[] hash = Digestion.digest(ByteBuffer.allocate(4).putInt(billId));
            byte[] signature = Cipheriscope.encrypt(app.getSignKey(), hash);

            app.write(new ValidateBillPacket(app.getSession(), billId, signature));
            ValidateBillResponsePacket p = app.readSpecific(ValidateBillResponsePacket.class);
            if(!p.wasValid()) {
                app.alert(Alert.AlertType.ERROR, "Signature was invalid", this).showAndWait();
            }

            billField.setText("");
            nextBill.setDisable(false);
            validateField.setDisable(true);
        });
    }

}
