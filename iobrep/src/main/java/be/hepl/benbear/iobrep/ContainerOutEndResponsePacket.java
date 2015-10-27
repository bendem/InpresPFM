package be.hepl.benbear.iobrep;

public class ContainerOutEndResponsePacket extends ResponsePacket {

    public ContainerOutEndResponsePacket(String reason) {
        super(PacketId.CONTAINER_OUT_END_RESPONSE, reason);
    }

}
