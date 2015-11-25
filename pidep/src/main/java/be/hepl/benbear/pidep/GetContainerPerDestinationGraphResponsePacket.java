package be.hepl.benbear.pidep;

public class GetContainerPerDestinationGraphResponsePacket extends Packet {

    private final byte[] bytes;

    public GetContainerPerDestinationGraphResponsePacket(byte[] bytes) {
        super(Id.GetContainerPerDestinationGraphResponse);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
