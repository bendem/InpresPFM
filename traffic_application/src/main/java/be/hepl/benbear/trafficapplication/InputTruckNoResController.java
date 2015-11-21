package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.commons.generics.Tuple;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class InputTruckNoResController implements Initializable {

    private final MainApplication app;
    private MainController mainController;

    @FXML private TextField truckTextField;
    @FXML private TextField containerTextField;
    @FXML private TextField contentTextField;
    @FXML private Button addContainerButton;
    @FXML private Button confirmButton;
    @FXML private ListView<Tuple<String, String>> containerListView;

    public InputTruckNoResController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addContainerButton.setOnAction(e -> onAddContainer());
        confirmButton.setOnAction(e -> onConfirm());
        containerListView.setCellFactory(param -> new ListCell<Tuple<String, String>>(){
            @Override
            public void updateItem(Tuple<String, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getFirst() + " | " + item.getSecond());
                }
            }
        });
    }

    public void setMainController(MainController ctrl) {
        this.mainController = ctrl;
    }

    private void onConfirm() {
        // Todo send Input_Lorry_Without_Reservation after check for values
    }

    private void onAddContainer() {
        if (!containerTextField.getText().isEmpty() && !contentTextField.getText().isEmpty()) {
            containerListView.getItems().add(new Tuple<>(containerTextField.getText(), contentTextField.getText()));
            containerTextField.clear();
            contentTextField.clear();
        }
    }
}

