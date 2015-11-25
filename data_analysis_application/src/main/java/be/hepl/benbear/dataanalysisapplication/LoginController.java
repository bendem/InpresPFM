package be.hepl.benbear.dataanalysisapplication;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final DataAnalysisApplication app;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    private DataAnalysisController chatController;

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

        Platform.runLater(() -> {
            // This is executed on the next application tick so that
            // all constructors have been called and everything has been
            // initialized (mainly, app.getStage is not available while
            // the application is starting).
            app.getStage(this).setOnCloseRequest(e -> {
                e.consume();
                app.close();
            });
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
        String username = usernameField.getText().trim();
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

    public void setDataAnalysisController(DataAnalysisController chatController) {
        this.chatController = chatController;
    }
}
