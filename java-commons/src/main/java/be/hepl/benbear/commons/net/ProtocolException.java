package be.hepl.benbear.commons.net;

import java.io.IOException;

public class ProtocolException extends IOException {

    public ProtocolException() {
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }

}
