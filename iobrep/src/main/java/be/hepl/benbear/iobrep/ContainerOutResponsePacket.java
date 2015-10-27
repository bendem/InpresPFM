package be.hepl.benbear.iobrep;

public class ContainerOutResponsePacket extends ResponsePacket {

    public ContainerOutResponsePacket(String reason) {
        super(PacketId.CONTAINER_OUT_RESPONSE, reason);
    }

}
