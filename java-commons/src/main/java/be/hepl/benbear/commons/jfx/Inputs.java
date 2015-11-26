package be.hepl.benbear.commons.jfx;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;

public final class Inputs {

    private Inputs() {}

    public static void integer(TextInputControl input, int min, int max) {
        if(input.getText().isEmpty()) {
            input.setText(String.valueOf(min));
        }
        input.textProperty().addListener((obs, o, n) -> {
            if(n.isEmpty()) {
                input.setText(String.valueOf(min));
            }
        });

        input.setOnKeyTyped(e -> {
            String character = e.getCharacter();
            if(character.length() != 1 || character.charAt(0) < '0' || character.charAt(0) > '9') {
                e.consume();
            }
        });

        input.setOnKeyPressed(e -> {
            if(e.getCode() != KeyCode.UP && e.getCode() != KeyCode.DOWN) {
                return;
            }

            int current = input.getText().isEmpty() ? 0 : Integer.parseInt(input.getText());
            int newCount = (e.getCode() == KeyCode.UP ? 1 : -1) * (e.isControlDown() ? 10 : 1) + current;

            input.setText(String.valueOf(Math.max(min, Math.min(max, newCount))));
            e.consume();
        });
    }

}
