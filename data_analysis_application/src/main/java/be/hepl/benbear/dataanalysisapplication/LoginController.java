package be.hepl.benbear.dataanalysisapplication;

import be.hepl.benbear.commons.security.Digestion;
import be.hepl.benbear.pidep.LoginPacket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final DataAnalysisApplication app;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    public LoginController(DataAnalysisApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setOnAction(e -> {
            if(!preLogin()) {
                onLogin();
            }
        });
    }

    /**
     * Checks for basic error before handling login.
     *
     * @return true if there were any error, false otherwise
     */
    private boolean preLogin() {
        boolean error = false;
        resetError();
        if(usernameField.getText().isEmpty()) {
            error("You need to provide your username.", usernameField);
            error = true;
        }
        if(passwordField.getText().isEmpty()) {
            error("You need to provide your password.", passwordField);
            error = true;
        }
        return error;
    }

    private void onLogin() {
        loginButton.setDisable(true);

        long time = Instant.now().toEpochMilli();
        byte[] salt = Digestion.salt(32);
        String password = passwordField.getText();
        String username = usernameField.getText().trim();

        LoginPacket packet = new LoginPacket(username, time, salt, Digestion.digest(password, time, salt));
        app.send(packet);
    }

    public void error(String error, Control... controls) {
        for(Control control : controls) {
            control.getStyleClass().add("error");
        }
        errorLabel.setText(errorLabel.getText() + '\n' + error);
    }

    public void resetError() {
        errorLabel.setText("");
        usernameField.getStyleClass().remove("error");
        passwordField.getStyleClass().remove("error");
        loginButton.setDisable(false);
    }

}
