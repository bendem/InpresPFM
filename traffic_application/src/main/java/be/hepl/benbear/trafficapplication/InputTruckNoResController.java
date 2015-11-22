package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.protocol.tramap.InputLorryPacket;
import be.hepl.benbear.protocol.tramap.InputLorryWithoutReservationPacket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InputTruckNoResController implements Initializable {

    private final MainApplication app;
    private MainController mainController;

    @FXML private TextField truckTextField;
    @FXML private TextField companyTextField;
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
        if (truckTextField.getText().isEmpty()) {
            //Todo throw some error
        }

        if (companyTextField.getText().isEmpty()) {
            //Todo throw some error
        }

        if (containerListView.getItems().isEmpty()) {
            //Todo throw some error
        }

        try {
            app.write(new InputLorryWithoutReservationPacket(truckTextField.getText(), companyTextField.getText(),
                containerListView.getItems().stream()
                    .map(Tuple::getFirst).toArray(String[]::new),
                containerListView.getItems().stream()
                    .map(Tuple::getSecond).toArray(String[]::new)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Read will be in ResultInputController
            app.open("ResultInput.fxml", "Input result", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        app.getStage(this).close();
    }

    private void onAddContainer() {
        if (!containerTextField.getText().isEmpty() && !contentTextField.getText().isEmpty()) {
            containerListView.getItems().add(new Tuple<>(containerTextField.getText(), contentTextField.getText()));
            containerTextField.clear();
            contentTextField.clear();
        }
    }
}

