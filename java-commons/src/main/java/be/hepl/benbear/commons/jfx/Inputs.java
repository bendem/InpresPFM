package be.hepl.benbear.commons.jfx;

import javafx.application.Platform;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Inputs {

    private Inputs() {}

    public static void integer(TextInputControl input, int min, int max) {
        if(input.getText().isEmpty()) {
            input.setText(String.valueOf(min));
        }
        input.textProperty().addListener((obs, o, n) -> {
            if(n.isEmpty()) {
                input.setText(String.valueOf(min));
                return;
            }

            try {
                int clamped = clamp(Integer.parseInt(n), min, max);
                if(!n.equals(String.valueOf(clamped))) { // Don't rely on the implementation preventing recursion
                    input.setText(String.valueOf(clamped));
                }
            } catch(NumberFormatException e) {
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

            input.setText(String.valueOf(clamp(newCount, min, max)));
            e.consume();
        });
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static void blink(ScheduledExecutorService pool, TextInputControl... inputs) {
        for(TextInputControl input : inputs) {
            input.getStyleClass().add("error");
        }

        pool.schedule(() -> Platform.runLater(() -> {
            for(TextInputControl input : inputs) {
                input.getStyleClass().removeAll("error");
            }
        }), 500, TimeUnit.MILLISECONDS);
    }

}
