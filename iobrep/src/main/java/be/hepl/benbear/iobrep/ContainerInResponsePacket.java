package be.hepl.benbear.iobrep;

public class ContainerInResponsePacket extends ResponsePacket {

    private final Container container;

    public ContainerInResponsePacket(String reason, Container container) {
        super(PacketId.CONTAINER_IN_RESPONSE, reason);
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }

}
