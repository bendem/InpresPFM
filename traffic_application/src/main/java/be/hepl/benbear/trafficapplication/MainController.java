package be.hepl.benbear.trafficapplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final MainApplication app;
    @FXML private Button loginButton;
    @FXML private Button truckResButton;
    @FXML private Button truckNoResButton;
    @FXML private Button listButton;

    public MainController(MainApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDisable(true);

        loginButton.setOnAction(e -> {
            if (app.isConnected()) {
                onLogout();
            } else {
                onLogin();
            }
        });


        truckResButton.setOnAction(e -> onTruckRes());
        truckNoResButton.setOnAction(e -> onTruckNoRes());
        listButton.setOnAction(e -> onList());
    }

    private void onLogin() {
        try {
            LoginController controller = app.open("Login.fxml", "Login", true);
            controller.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onLogout() {
        app.setConnected(false);
    }

    private void onTruckRes() {
        try {
            InputTruckResController controller = app.open("InputTruckRes.fxml", "Arrival with reservation", true);
            controller.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onTruckNoRes() {
        try {
            InputTruckNoResController controller = app.open("InputTruckNoRes.fxml", "Arrival without reservation", true);
            controller.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onList() {
        try {
            SearchController controller = app.open("Search.fxml", "Search for movements", true);
            controller.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDisable(boolean b){
        truckResButton.setDisable(b);
        truckNoResButton.setDisable(b);
        listButton.setDisable(b);
        loginButton.setText(b ? "Login" : "Logout");
    }
}
