package be.hepl.benbear.mail;

import be.hepl.benbear.commons.jfx.Inputs;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.Message;

public class ComposeController implements Initializable {

    private final MailApplication app;
    @FXML private TextField toField;
    @FXML private TextField ccField;
    @FXML private TextField bccField;
    @FXML private TextField subjectField;
    @FXML private TextArea contentField;
    @FXML private Button sendButton;

    public ComposeController(MailApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sendButton.setOnAction(this::onSend);
    }

    public void answer(Mail mail) {
        toField.setText(String.join(", ", mail.getFrom()));
        subjectField.setText("RE: " + mail.getSubject());
    }

    private void onSend(ActionEvent e) {
        String to = toField.getText().trim();
        String cc = ccField.getText().trim();
        String bcc = bccField.getText().trim();
        if(to.isEmpty() && cc.isEmpty() && bcc.isEmpty()) {
            Inputs.blink(app.getThreadPool(), toField, ccField, bccField);
            return;
        }

        String subject = subjectField.getText().trim();
        if(subject.isEmpty()) {
            Inputs.blink(app.getThreadPool(), subjectField);
            return;
        }

        String content = contentField.getText();
        if(content.trim().isEmpty()) {
            Inputs.blink(app.getThreadPool(), contentField);
            return;
        }

        Map<Message.RecipientType, String[]> recipients = new HashMap<>();

        if(!to.isEmpty()) {
            recipients.put(Message.RecipientType.TO, to.split("\\s*,\\s*"));
        }
        if(!cc.isEmpty()) {
            recipients.put(Message.RecipientType.CC, cc.split("\\s*,\\s*"));
        }
        if(!bcc.isEmpty()) {
            recipients.put(Message.RecipientType.BCC, bcc.split("\\s*,\\s*"));
        }

        app.send(recipients, subject, content)
            .whenComplete((v, throwable) -> Platform.runLater(() -> {
                if(throwable != null) {
                    throwable.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something bad happened");
                    alert.initOwner(app.getStage(this));
                    alert.show();
                } else {
                    app.getStage(this).close();
                }
            }));

        // TODO visual feedback
    }

}
