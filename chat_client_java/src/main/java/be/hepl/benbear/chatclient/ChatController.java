package be.hepl.benbear.chatclient;

import be.hepl.benbear.pfmcop.QuestionPacket;
import be.hepl.benbear.pfmcop.UDPPacket;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class ChatController implements Initializable {

    private final ChatApplication app;
    @FXML private ListView<Message> chatList;
    @FXML private TextField inputField;
    @FXML private Button questionButton;
    @FXML private Button answerButton;
    @FXML private Button eventButton;
    private String username;

    public ChatController(ChatApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObjectProperty<UUID> selectedUuid = new SimpleObjectProperty<>();

        chatList.setPlaceholder(new Label("No content yet."));
        chatList.setCellFactory(l -> new MessageCell(selectedUuid));
        chatList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        chatList.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            answerButton.setDisable(n == null || n.getType() != UDPPacket.Type.QUESTION);
            selectedUuid.setValue(n == null ? null : n.getUuid());
        });
        chatList.setItems(app.messagesProperty());

        inputField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER && e.isControlDown()) {
                e.consume();
                answerButton.fire();
            }
        });

        questionButton.setOnAction(e -> {
            String text = inputField.getText();
            if(text.isEmpty()) {
                return;
            }
            inputField.setText("");
            UUID tag = UUID.randomUUID();
            byte[] digest = QuestionPacket.digest(username, text, tag);
            app.send(new QuestionPacket(username, text, tag, digest));
        });
        answerButton.setOnAction(e -> {
            String text = inputField.getText();
            if(text.isEmpty()) {
                return;
            }
            inputField.setText("");
            Message message = chatList.getSelectionModel().getSelectedItem();
            app.send(new UDPPacket(UDPPacket.Type.ANSWER, username, text, message.getUuid()));
        });
        eventButton.setOnAction(e -> {
            String text = inputField.getText();
            if(text.isEmpty()) {
                return;
            }
            inputField.setText("");
            app.send(new UDPPacket(UDPPacket.Type.EVENT, username, text, null));
        });
    }

    public void setUsername(String username) {
        this.username = username;
        inputField.setDisable(false);
        questionButton.setDisable(false);
        eventButton.setDisable(false);
    }

}
