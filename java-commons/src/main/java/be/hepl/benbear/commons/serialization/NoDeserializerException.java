package be.hepl.benbear.commons.serialization;

public class NoDeserializerException extends SerializationException {

    public NoDeserializerException() {
    }

    public NoDeserializerException(String message) {
        super(message);
    }

    public NoDeserializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDeserializerException(Throwable cause) {
        super(cause);
    }

    public NoDeserializerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
