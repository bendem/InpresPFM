package be.hepl.benbear.pidep;

public class ErrorPacket extends Packet{

    private final String message;

    public ErrorPacket(String message) {
        super(Id.Error);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
