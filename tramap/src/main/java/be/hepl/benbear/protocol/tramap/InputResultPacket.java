package be.hepl.benbear.protocol.tramap;

import java.util.List;

public interface InputResultPacket {
    boolean isOk();
    String getReason();
    List<ContainerPosition> getContainers();
}
