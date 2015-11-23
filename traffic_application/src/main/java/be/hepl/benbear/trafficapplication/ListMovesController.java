package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.protocol.tramap.ListOperationsResponsePacket;
import be.hepl.benbear.protocol.tramap.Movement;
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
import java.time.Instant;
import java.util.ResourceBundle;

public class ListMovesController implements Initializable {

    private final MainApplication app;
    private MainController mainController;
    private ListOperationsResponsePacket packet;

    @FXML private Label descLabel;
    @FXML private TableView<Movement> movementsTableView;
    @FXML private TableColumn<Movement, Number> idColumn;
    @FXML private TableColumn<Movement, String> containerIdColumn;
    @FXML private TableColumn<Movement, String> destinationColumn;
    @FXML private TableColumn<Movement, String> companyColumn;
    @FXML private TableColumn<Movement, Instant> arrivalColumn;
    @FXML private TableColumn<Movement, Instant> departureColumn;
    @FXML private Button okButton;

    public ListMovesController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(f -> new ReadOnlyIntegerWrapper(f.getValue().getMovementId()));
        containerIdColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getContainerId()));
        destinationColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getDestination()));
        companyColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getCompanyName()));
        arrivalColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(Instant.ofEpochMilli(f.getValue().getDateArrival())));
        departureColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(Instant.ofEpochMilli(f.getValue().getDateDeparture())));
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
