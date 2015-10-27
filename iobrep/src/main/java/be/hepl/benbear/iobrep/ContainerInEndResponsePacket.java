package be.hepl.benbear.iobrep;

public class ContainerInEndResponsePacket extends ResponsePacket {

    public ContainerInEndResponsePacket(String reason) {
        super(PacketId.CONTAINER_IN_END_RESPONSE, reason);
    }

}
