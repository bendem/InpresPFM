package be.hepl.benbear.commons.serialization;

public class NoSerializerException extends SerializationException {

    public NoSerializerException() {
    }

    public NoSerializerException(String message) {
        super(message);
    }

    public NoSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSerializerException(Throwable cause) {
        super(cause);
    }

    public NoSerializerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
