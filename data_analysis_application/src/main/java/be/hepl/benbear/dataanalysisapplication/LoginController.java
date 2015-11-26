package be.hepl.benbear.dataanalysisapplication;

import be.hepl.benbear.pidep.LoginPacket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final DataAnalysisApplication app;
    private final Random random;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    public LoginController(DataAnalysisApplication app) {
        this.app = app;
        this.random = new SecureRandom();
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
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        String password = passwordField.getText();
        String username = usernameField.getText().trim();

        LoginPacket packet = new LoginPacket(username, time, salt, digest(password, time, salt));
        app.send(packet);
    }

    private byte[] digest(String password, long time, byte[] salt) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);
        try {
            dos.writeLong(time);
            dos.write(salt);
            dos.write(password.getBytes());
        } catch(IOException e) {}
        return LoginPacket.digest(boas.toByteArray());
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
