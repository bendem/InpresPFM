package be.hepl.benbear.iobrep;

import java.util.List;

public class GetContainersResponsePacket extends ResponsePacket {

    private final List<Container> containers;

    public GetContainersResponsePacket(String reason, List<Container> containers) {
        super(PacketId.GET_CONTAINERS_RESPONSE, reason);
        this.containers = containers;
    }

    public List<Container> getContainers() {
        return containers;
    }

}
