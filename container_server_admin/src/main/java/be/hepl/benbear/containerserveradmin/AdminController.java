package be.hepl.benbear.containerserveradmin;

import be.hepl.benbear.commons.jfx.Inputs;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.containerserveradmin.proto.ListPacket;
import be.hepl.benbear.containerserveradmin.proto.ListResponsePacket;
import be.hepl.benbear.containerserveradmin.proto.PausePacket;
import be.hepl.benbear.containerserveradmin.proto.PauseReponsePacket;
import be.hepl.benbear.containerserveradmin.proto.StopPacket;
import be.hepl.benbear.containerserveradmin.proto.StopResponsePacket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private final ContainerServerAdmin app;
    @FXML private ListView<String> ipList;
    @FXML private Button refreshButton;
    @FXML private TextField timeField;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;

    public AdminController(ContainerServerAdmin app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshButton.setOnAction(e -> app.send(new ListPacket(), ListResponsePacket.class)
            .whenComplete((response, exc) -> Platform.runLater(() -> {
                if(exc != null) {
                    Log.e("Error fetching information", exc);
                    return;
                }
                ipList.getItems().setAll(response.getIps());
            })));

        stopButton.setOnAction(e -> app.send(new StopPacket(Integer.parseInt(timeField.getText())), StopResponsePacket.class)
            .whenComplete((response, exc) -> Platform.runLater(() -> {
                if(exc != null) {
                    Log.e("Error stopping the server", exc);
                    return;
                }
                if(response.getError().isEmpty()) {
                    // TODO Display success
                    Log.i("\\o/");
                } else {
                    Log.w("Couldn't stop the server: %s", response.getError());
                }
            })));
        pauseButton.setOnAction(e -> app.send(new PausePacket(), PauseReponsePacket.class)
            .whenComplete((response, exc) -> Platform.runLater(() -> {
                if(exc != null) {
                    Log.e("Error stopping the server", exc);
                    return;
                }
                if(response.getError().isEmpty()) {
                    // TODO Display success
                    Log.i("\\o/");
                } else {
                    Log.w("Couldn't stop the server: %s", response.getError());
                }
            })));

        Inputs.integer(timeField, 0, Integer.MAX_VALUE);
    }

    public void unlock() {
        refreshButton.setDisable(false);
        timeField.setDisable(false);
        stopButton.setDisable(false);
        pauseButton.setDisable(false);
    }

}
