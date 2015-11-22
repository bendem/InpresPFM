package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.protocol.tramap.ListOperationsPacket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    private final MainApplication app;
    private MainController mainController;

    @FXML private Button searchButton;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ToggleGroup typeSearch;
    @FXML private TextField criteriaTextField;
    @FXML private RadioButton companyRadio;
    @FXML private RadioButton destinationRadio;

    public SearchController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchButton.setOnAction(e -> onSearch());
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
    }

    public void setMainController(MainController ctrl) {
        this.mainController = ctrl;
    }

    private void onSearch() {
        Instant startInstant = startDatePicker.getValue().atStartOfDay(ZoneOffset.systemDefault()).toInstant();
        Instant endInstant = startDatePicker.getValue().atStartOfDay(ZoneOffset.systemDefault()).toInstant();

        if (startInstant.isAfter(endInstant)) {
            //Todo throw some error ? :L
        }

        if (criteriaTextField.getText().isEmpty()){
            //Todo throw some other error ? :L
        }

        //Todo send List_Operation packet

        try {
            app.write(new ListOperationsPacket(startInstant.getEpochSecond(), endInstant.getEpochSecond(), criteriaTextField.getText(),
                typeSearch.getSelectedToggle() == companyRadio ? ListOperationsPacket.Type.Society.toString() : ListOperationsPacket.Type.Destination.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Read will be in ListMovesController
            app.open("ListMoves.fxml", "List of operations", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        app.getStage(this).close();
    }
}
