package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.protocol.tramap.ListOperationsResponsePacket;
import be.hepl.benbear.protocol.tramap.Movement;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListMovesController implements Initializable {

    private final MainApplication app;
    private MainController mainController;
    private ListOperationsResponsePacket packet;

    @FXML private Button okButton;
    @FXML private TableView<Movement> movementsTableView;
    @FXML private Label descLabel;

    public ListMovesController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        okButton.setOnAction(e -> onOk());

        try {
            packet = (ListOperationsResponsePacket) app.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (packet.isOk()) {
            descLabel.setText("List of operations");
            movementsTableView.getItems().addAll(packet.getMovements());
        } else {
            descLabel.setText("Failure (" + packet.getReason() + ")");
        }
    }

    public void setMainController(MainController ctrl) {
        this.mainController = ctrl;
    }

    private void onOk() {
        app.getStage(this).close();
    }

}
