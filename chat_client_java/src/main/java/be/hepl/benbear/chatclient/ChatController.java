package be.hepl.benbear.chatclient;

import be.hepl.benbear.pfmcop.UDPPacket;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ChatController implements Initializable {

    private static final int HISTORY_SIZE = 20;

    private final ChatApplication app;
    private final List<String> sentMessages;
    private int sentMessagesIndex;
    @FXML private ListView<Message> chatList;
    @FXML private TextField inputField;
    @FXML private Button questionButton;
    @FXML private Button answerButton;
    @FXML private Button eventButton;
    private String username;

    public ChatController(ChatApplication app) {
        this.app = app;
        sentMessages = new ArrayList<>(HISTORY_SIZE);
        sentMessagesIndex = 0;
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
        chatList.getItems().addListener((ListChangeListener.Change<?extends Message> change) -> {
            chatList.scrollTo(change.getList().size());
        });

        inputField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                if(e.isControlDown()) {
                    e.consume();
                    answerButton.fire();
                } else if(e.isAltDown()) {
                    e.consume();
                    eventButton.fire();
                }
            } else if(e.getCode() == KeyCode.UP) {
                if(sentMessages.isEmpty() || sentMessagesIndex <= 0) {
                    return;
                }
                inputField.setText(sentMessages.get(--sentMessagesIndex));
                e.consume();
            } else if(e.getCode() == KeyCode.DOWN) {
                if(sentMessages.isEmpty() || sentMessagesIndex + 1 >= sentMessages.size()) {
                    return;
                }
                inputField.setText(sentMessages.get(++sentMessagesIndex));
                e.consume();
            }
        });

        questionButton.setOnAction(e -> {
            String text = inputField.getText();
            if(text.isEmpty()) {
                return;
            }
            UUID tag = UUID.randomUUID();
            byte[] digest = UDPPacket.digest(username, text, tag);
            send(new UDPPacket(UDPPacket.Type.QUESTION, username, text, tag, digest));
        });
        answerButton.setOnAction(e -> {
            String text = inputField.getText();
            if(text.isEmpty()) {
                return;
            }
            Message message = chatList.getSelectionModel().getSelectedItem();
            // TODO Possibly check digest here instead of on receive?
            send(new UDPPacket(UDPPacket.Type.ANSWER, username, text, message.getUuid(), null));
        });
        eventButton.setOnAction(e -> {
            String text = inputField.getText();
            if(text.isEmpty()) {
                return;
            }
            send(new UDPPacket(UDPPacket.Type.EVENT, username, text, null, null));
        });
    }

    private void send(UDPPacket packet) {
        if(sentMessages.size() == HISTORY_SIZE) {
            sentMessages.remove(0);
        }
        sentMessages.add(inputField.getText());
        sentMessagesIndex = sentMessages.size();
        inputField.setText("");
        app.send(packet);
    }

    public void setUsername(String username) {
        this.username = username;
        inputField.setDisable(false);
        questionButton.setDisable(false);
        eventButton.setDisable(false);
    }

}
