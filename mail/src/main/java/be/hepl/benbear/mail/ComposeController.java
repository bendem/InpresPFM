package be.hepl.benbear.mail;

import be.hepl.benbear.commons.jfx.Inputs;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.Message;

public class ComposeController implements Initializable {

    private final MailApplication app;
    private final ObservableSet<File> attachedFiles;
    @FXML private TextField toField;
    @FXML private TextField ccField;
    @FXML private TextField bccField;
    @FXML private TextField subjectField;
    @FXML private HTMLEditor contentField;
    @FXML private ListView<String> attachedList;
    @FXML private Button attachButton;
    @FXML private Button sendButton;

    public ComposeController(MailApplication app) {
        this.app = app;
        this.attachedFiles = FXCollections.observableSet();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attachedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        attachedList.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.DELETE) {
                attachedFiles.removeIf(f -> attachedList.getSelectionModel().getSelectedItems().contains(f.getName()));
            }
        });
        attachedFiles.addListener((SetChangeListener.Change<? extends File> change) -> {
            if(change.wasAdded()) {
                attachedList.getItems().add(change.getElementAdded().getName());
            }
            if(change.wasRemoved()) {
                attachedList.getItems().remove(change.getElementRemoved().getName());
            }
        });
        attachButton.setOnAction(this::onAttach);
        sendButton.setOnAction(this::onSend);
    }

    public void answer(Mail mail) {
        toField.setText(String.join(", ", mail.getFrom()));
        subjectField.setText("RE: " + mail.getSubject());
    }

    private void onAttach(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        attachedFiles.addAll(chooser.showOpenMultipleDialog(app.getStage(this)));
    }

    private void onSend(ActionEvent e) {
        attachButton.setDisable(true);
        sendButton.setDisable(true);
        String to = toField.getText().trim();
        String cc = ccField.getText().trim();
        String bcc = bccField.getText().trim();
        if(to.isEmpty() && cc.isEmpty() && bcc.isEmpty()) {
            Inputs.blink(app.getThreadPool(), toField, ccField, bccField);
            attachButton.setDisable(false);
            sendButton.setDisable(false);
            return;
        }

        String subject = subjectField.getText().trim();
        if(subject.isEmpty()) {
            Inputs.blink(app.getThreadPool(), subjectField);
            attachButton.setDisable(false);
            sendButton.setDisable(false);
            return;
        }

        String content = contentField.getHtmlText().replace("contenteditable=\"true\"", "");
        if(content.trim().isEmpty()) {
            Inputs.blink(app.getThreadPool(), contentField);
            attachButton.setDisable(false);
            sendButton.setDisable(false);
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

        app.send(recipients, subject, content, attachedFiles)
            .whenComplete((v, throwable) -> Platform.runLater(() -> {
                if(throwable != null) {
                    throwable.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something bad happened");
                    alert.initOwner(app.getStage(this));
                    alert.show();
                    attachButton.setDisable(false);
                    sendButton.setDisable(false);
                } else {
                    app.getStage(this).close();
                }
            }));
    }

}
