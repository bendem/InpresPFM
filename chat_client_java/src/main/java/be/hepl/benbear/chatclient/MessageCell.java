package be.hepl.benbear.chatclient;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

import java.util.UUID;

public class MessageCell extends ListCell<Message> {

    private static final int TAG_LENGTH = 9;
    private static final int USERNAME_LENGTH = 10;
    private final ObservableValue<UUID> selectedUuid;

    public MessageCell(ObservableValue<UUID> selectedUuid) {
        this.selectedUuid = selectedUuid;
        setFont(Font.font("monospace", 15));
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setText("");
            return;
        }

        setText(fixedLength(item.type.name().toLowerCase() + ':', TAG_LENGTH)
            + ' ' + fixedLength(item.username, USERNAME_LENGTH)
            + ' ' + item.message);

        if(item.getUuid() == null) {
            return;
        }

        if(item.getUuid().equals(selectedUuid.getValue())) {
            getStyleClass().add("matching");
        }

        selectedUuid.removeListener(this::uuidChangeListener);
        selectedUuid.addListener(this::uuidChangeListener);
    }

    private void uuidChangeListener(ObservableValue<? extends UUID> obs, UUID old, UUID n) {
        if(getItem() == null || getItem().getUuid() == null) {
            selectedUuid.removeListener(this::uuidChangeListener);
            getStyleClass().remove("matching");
            return;
        }
        if(n == null) {
            getStyleClass().remove("matching");
            return;
        }
        if(getItem().getUuid().equals(n)) {
            getStyleClass().add("matching");
        } else {
            getStyleClass().remove("matching");
        }
    }

    private String fixedLength(String str, int len) {
        if(str.length() == len) {
            return str;
        }

        StringBuilder sb = new StringBuilder(len);
        if(str.length() < len) {
            sb.append(str);
            for(int i = str.length(); i < len; ++i) {
                sb.append(' ');
            }
        } else {
            sb.append(str.toCharArray(), 0, len);
            sb.setCharAt(len - 1, '+');
        }
        return sb.toString();
    }

}
