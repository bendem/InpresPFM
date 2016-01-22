package be.hepl.benbear.accountingclient;


import be.hepl.benbear.bisamap.LoginPacket;
import be.hepl.benbear.bisamap.LoginResponsePacket;
import be.hepl.benbear.commons.security.Cipheriscope;
import be.hepl.benbear.commons.security.Digestion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.Instant;
import java.util.ResourceBundle;

import javax.crypto.SecretKey;

public class LoginController implements Initializable {

    private final AccountingApplication app;
    private AccountingController mainController;

    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    public LoginController(AccountingApplication app) {
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
        long time = Instant.now().toEpochMilli();
        byte[] salt = Digestion.salt(32);
        byte[] digest = Digestion.digest(passwordField.getText(), time, salt);

        SecretKey signKey = Cipheriscope.generateKey();
        SecretKey cryptKey = Cipheriscope.generateKey();

        Certificate certificate;
        try {
            certificate = CertificateFactory.getInstance("X.509").generateCertificate(Files.newInputStream(Paths.get(
                app.getConfig().getStringThrowing("accounting_server.public_key.path"))));
        } catch(CertificateException | IOException e) {
            throw new RuntimeException(e);
        }

        byte[] signKeyCiphered = Cipheriscope.encrypt(certificate.getPublicKey(), signKey.getEncoded());
        byte[] cryptKeyCiphered = Cipheriscope.encrypt(certificate.getPublicKey(), cryptKey.getEncoded());

        app.write(new LoginPacket(usernameField.getText(), time, salt, digest, signKeyCiphered, cryptKeyCiphered));

        LoginResponsePacket p = app.readSpecific(LoginResponsePacket.class);
        if (p.getSession() != null) {
            app.connect(p.getSession(), signKey, cryptKey);
            app.getStage(this).close();
        } else {
            error("Invalid login/password", usernameField, passwordField);
        }
    }

    private void error(String error, Control... controls) {
        for(Control control : controls) {
            control.getStyleClass().add("error");
        }

        errorLabel.setText(errorLabel.getText() + '\n' + error);
    }

    private void resetError() {
        errorLabel.setText("");
        usernameField.getStyleClass().remove("error");
        passwordField.getStyleClass().remove("error");
    }

    public void setMainController(AccountingController ctrl) {
        this.mainController = ctrl;
    }

}
