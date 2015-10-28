package be.hepl.benbear.boatapp;

import java.io.IOException;

public class ProtocolException extends IOException {
    public ProtocolException() {
    }

    public ProtocolException(String detailMessage) {
        super(detailMessage);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}
