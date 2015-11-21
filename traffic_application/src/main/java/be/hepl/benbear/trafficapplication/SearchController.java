package be.hepl.benbear.trafficapplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
        if (typeSearch.getSelectedToggle() == companyRadio) {
            //send with company type as type
        } else {
            //send with destination type as type
        }

    }
}
