package be.hepl.benbear.containerserveradmin;

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
import javafx.scene.input.KeyCode;

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

        stopButton.setOnAction(e -> app.send(new StopPacket(getTime()), StopResponsePacket.class)
            .whenComplete((response, exc) -> Platform.runLater(() -> {
                if(exc != null) {
                    Log.e("Error stopping the server", exc);
                    return;
                }
                if(response.getError() != null) {
                    Log.w("Couldn't stop the server: %s", response.getError());
                } else {
                    // TODO Display success
                    Log.i("\\o/");
                }
            })));
        pauseButton.setOnAction(e -> app.send(new PausePacket(), PauseReponsePacket.class)
            .whenComplete((response, exc) -> Platform.runLater(() -> {
                if(exc != null) {
                    Log.e("Error stopping the server", exc);
                    return;
                }
                if(response.getError() != null) {
                    Log.w("Couldn't stop the server: %s", response.getError());
                } else {
                    // TODO Display success
                    Log.i("\\o/");
                }
            })));

        // Setup the time field for integer values only
        timeField.textProperty().addListener((obs, o, n) -> {
            if(n.isEmpty()) {
                timeField.setText("0");
            }
        });
        timeField.setOnKeyTyped(e -> {
            String character = e.getCharacter();
            if(character.length() != 1 || character.charAt(0) < '0' || character.charAt(0) > '9') {
                e.consume();
            }
        });
        timeField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
                changeTime(e.getCode() == KeyCode.UP, e.isControlDown() ? 10 : 1);
                e.consume();
            }
        });
    }

    private void changeTime(boolean inc, int count) {
        int newCount = (inc ? 1 : -1) * count + getTime();
        timeField.setText(String.valueOf(newCount < 0 ? 0 : newCount));
    }

    private int getTime() {
        return timeField.getText().isEmpty() ? 0 : Integer.parseInt(timeField.getText());
    }

    public void unlock() {
        refreshButton.setDisable(false);
        timeField.setDisable(false);
        stopButton.setDisable(false);
        pauseButton.setDisable(false);
    }

}
