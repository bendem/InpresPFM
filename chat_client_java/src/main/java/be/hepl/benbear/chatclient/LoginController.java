package be.hepl.benbear.chatclient;

import be.hepl.benbear.commons.logging.Log;
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

    private final ChatApplication app;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    private ChatController chatController;

    public LoginController(ChatApplication app) {
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

        app.checkLogin(username, passwordField.getText().trim())
            .whenComplete((res, exc) -> Platform.runLater(() -> {
                if(exc != null) {
                    error("An error happened: " + exc.getMessage());
                    Log.e("Error while logging in", exc);
                    return;
                }

                if(res.first == null || res.first.isEmpty()) {
                    error("Invalid username or password", usernameField, passwordField);
                } else {
                    app.startChat(res.first, res.second);
                    app.getStage(this).close();
                    chatController.setUsername(username);
                }
            }));
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

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }
}
