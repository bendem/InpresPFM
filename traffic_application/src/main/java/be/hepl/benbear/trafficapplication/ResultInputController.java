package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.protocol.tramap.ContainerPosition;
import be.hepl.benbear.protocol.tramap.InputResultPacket;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ResultInputController implements Initializable {

    private final MainApplication app;
    private MainController mainController;
    private InputResultPacket packet;
    @FXML private TableView<ContainerPosition> resultTableView;
    @FXML private TableColumn<ContainerPosition, String> idColumn;
    @FXML private TableColumn<ContainerPosition, Number> xColumn;
    @FXML private TableColumn<ContainerPosition, Number> yColumn;
    @FXML private Button okButton;
    @FXML private Label statusLabel;

    public ResultInputController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(feature -> new ReadOnlyObjectWrapper<>(feature.getValue().getContainerId()));
        xColumn.setCellValueFactory(feature -> new ReadOnlyIntegerWrapper(feature.getValue().getX()));
        yColumn.setCellValueFactory(feature -> new ReadOnlyIntegerWrapper(feature.getValue().getY()));

        okButton.setOnAction(e -> onOk());
        try {
            packet = (InputResultPacket) app.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (packet.isOk()) {
            statusLabel.setText("Status : Success");
            resultTableView.getItems().addAll(packet.getContainers());
        } else {
            statusLabel.setText("Status : Failure (" + packet.getReason() + ")");
        }
    }

    public void setMainController(MainController ctrl) {
        this.mainController = ctrl;
    }

    private void onOk() {
        app.getStage(this).close();
    }

}
