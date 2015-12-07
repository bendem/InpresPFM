package be.hepl.benbear.mail;

import be.hepl.benbear.commons.logging.Log;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.ResourceBundle;

public class MailController implements Initializable {

    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .toFormatter();

    private final MailApplication app;
    @FXML private SplitPane splitPane;
    @FXML private Node messageBox;
    @FXML private Button newMessageButton;
    @FXML private Button answerButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private TableView<Mail> messageTable;
    @FXML private TableColumn<Mail, String> fromTableColumn;
    @FXML private TableColumn<Mail, String> subjectTableColumn;
    @FXML private TableColumn<Mail, String> dateTableColumn;
    @FXML private Label toLabel;
    @FXML private Label sentDateLabel;
    @FXML private Label receivedDateLabel;
    @FXML private WebView webview;
    @FXML private ListView<String> attachmentsList;

    public MailController(MailApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SplitPane.setResizableWithParent(messageBox, false);

        newMessageButton.setOnAction(this::onNewMessage);
        answerButton.setOnAction(this::onAnswer);
        deleteButton.setOnAction(this::onDelete);
        refreshButton.setOnAction(this::onRefresh);

        messageTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if(n != null) {
                selected(n);
            } else {
                unselected();
            }
        });
        fromTableColumn.setCellValueFactory(fea -> new ReadOnlyStringWrapper(String.join(", ", fea.getValue().getFrom())));
        subjectTableColumn.setCellValueFactory(fea -> new ReadOnlyStringWrapper(fea.getValue().getSubject()));
        dateTableColumn.setCellValueFactory(fea -> new ReadOnlyStringWrapper(fea.getValue().getSent()
            .map(i -> i.atZone(ZoneId.systemDefault()))
            .map(DATE_FORMAT::format)
            .orElse("")));
        dateTableColumn.setSortType(TableColumn.SortType.DESCENDING);
        messageTable.getSortOrder().add(dateTableColumn);
        messageTable.sort();

        webview.getEngine().setJavaScriptEnabled(false);
        webview.getEngine().getLoadWorker().stateProperty().addListener((obs, o, n) -> {
            if(n == Worker.State.SUCCEEDED) {
                Log.d("Loaded stuff, injecting listeners");
                NodeList links = webview.getEngine().getDocument().getElementsByTagName("a");
                for(int i = 0; i < links.getLength(); i++) {
                    ((EventTarget) links.item(i)).addEventListener("click", evt -> {
                        evt.preventDefault();
                        org.w3c.dom.Node href = ((org.w3c.dom.Node) evt.getTarget()).getAttributes().getNamedItem("href");
                        if(href != null && !href.getTextContent().isEmpty()) {
                            Log.d("Opening " + href.getTextContent());
                            app.getHostServices().showDocument(href.getTextContent());
                        }
                    }, false);
                }
            }
        });

        attachmentsList.setOnMouseClicked(e -> {
            if(e.getButton() != MouseButton.PRIMARY || e.getClickCount() != 2) {
                return;
            }
            String attachment = attachmentsList.getSelectionModel().getSelectedItem();
            Mail mail = messageTable.getSelectionModel().getSelectedItem();

            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(attachment);
            File file = chooser.showSaveDialog(app.getStage(this));
            if(file != null) {
                try {
                    Files.write(file.toPath(), mail.getAttached().get(attachment));
                } catch(IOException e1) {
                    e1.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save file: " + e1.getMessage());
                    alert.initOwner(app.getStage(this));
                    alert.show();
                }
            }
        });
    }

    private void selected(Mail mail) {
        String content = mail.getContent();
        if(mail.getType() == Mail.Type.TEXT) {
            content = content.replace("\n", "<br>");
        }
        answerButton.setDisable(false);
        deleteButton.setDisable(false);

        toLabel.setText(String.join(", ", mail.getTo()));
        sentDateLabel.setText(mail.getSent()
            .map(i -> i.atZone(ZoneId.systemDefault()))
            .map(DATE_FORMAT::format).orElse(""));
        receivedDateLabel.setText(mail.getReceived()
            .map(i -> i.atZone(ZoneId.systemDefault()))
            .map(DATE_FORMAT::format).orElse(""));

        webview.getEngine().loadContent(content);

        attachmentsList.setVisible(!mail.getAttached().isEmpty());
        attachmentsList.getItems().setAll(mail.getAttached().keySet());
    }

    private void unselected() {
        answerButton.setDisable(true);
        deleteButton.setDisable(true);

        webview.getEngine().loadContent("");

        toLabel.setText("");
        sentDateLabel.setText("");
        receivedDateLabel.setText("");

        attachmentsList.getItems().clear();
        attachmentsList.setVisible(false);
    }

    private void onNewMessage(ActionEvent e) {
        try {
            app.open("Compose.fxml", "InpresFPM - Compose", false);
        } catch(IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    private void onAnswer(ActionEvent event) {
        try {
            app.<ComposeController>open("Compose.fxml", "InpresFPM - Compose", false)
                .answer(messageTable.getSelectionModel().getSelectedItem());
        } catch(IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    private void onDelete(ActionEvent event) {
        // TODO Delete the mail from local storage once that part is implemented
        messageTable.getItems().remove(messageTable.getSelectionModel().getSelectedItem());
    }

    private void onRefresh(ActionEvent event) {
        app.refresh();
    }

    public void addMessages(List<Mail> messages) {
        messageTable.getItems().addAll(messages);
        messageTable.sort();
    }

}
