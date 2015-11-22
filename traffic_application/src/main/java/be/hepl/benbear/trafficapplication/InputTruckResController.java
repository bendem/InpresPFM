package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.protocol.tramap.InputLorryPacket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
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

        try {
            app.write(new InputLorryPacket(reservationTextField.getText(), containerListView.getItems().stream().toArray(String[]::new)));
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
        if (!containerTextField.getText().isEmpty()) {
            containerListView.getItems().add(containerTextField.getText());
            containerTextField.clear();
        }
    }

}
