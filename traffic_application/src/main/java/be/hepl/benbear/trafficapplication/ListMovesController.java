package be.hepl.benbear.trafficapplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ListMovesController implements Initializable {

    private final MainApplication app;
    private MainController mainController;

    @FXML private Button okButton;
    @FXML private Button movementsTableView;
    @FXML private Label descLabel;

    public ListMovesController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        okButton.setOnAction(e -> onOk());
    }

    public void setMainController(MainController ctrl) {
        this.mainController = ctrl;
    }

    private void onOk() {
        app.getStage(this).close();
    }

}
