package be.hepl.benbear.iobrep;

public class ContainerOutResponsePacket extends ResponsePacket {

    private final String containerId;

    public ContainerOutResponsePacket(String containerId, String reason) {
        super(PacketId.CONTAINER_OUT_RESPONSE, reason);
        this.containerId = containerId;
    }

    public String getContainerId() {
        return containerId;
    }

}
