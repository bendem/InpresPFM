package be.hepl.benbear.trafficapplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class InputTruckResController implements Initializable {

    private final MainApplication app;
    private MainController mainController;

    @FXML private TextField reservationTextField;
    @FXML private TextField containerTextField;
    @FXML private Button addContainerButton;
    @FXML private Button confirmButton;
    @FXML private ListView<String> containerListView;

    public InputTruckResController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addContainerButton.setOnAction(e -> onAddContainer());
        confirmButton.setOnAction(e -> onConfirm());
    }

    public void setMainController(MainController ctrl) {
        this.mainController = ctrl;
    }

    private void onConfirm() {
        if (reservationTextField.getText().isEmpty()) {
            //Todo throw some error
        }

        if (containerListView.getItems().isEmpty()) {
            //Todo throw some error
        }
        // Todo send Input_Lorry after check for values
    }

    private void onAddContainer() {
        if (!containerTextField.getText().isEmpty()) {
            containerListView.getItems().add(containerTextField.getText());
            containerTextField.clear();
        }
    }

}
