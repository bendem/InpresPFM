package be.hepl.benbear.pidep;

public class GetContainerPerDestinationPerQuarterGraphResponsePacket extends Packet {

    private final byte[] bytes;

    public GetContainerPerDestinationPerQuarterGraphResponsePacket(byte[] bytes) {
        super(Id.GetContainerPerDestinationPerQuarterGraphResponse);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
