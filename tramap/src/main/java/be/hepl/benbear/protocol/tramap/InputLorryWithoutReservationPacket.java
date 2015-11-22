package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class InputLorryWithoutReservationPacket implements Packet {

    public static final byte ID = 3;

    private final String license;
    private final String companyName;
    private final String[] containerIds;
    private final String[] containerContents;

    public InputLorryWithoutReservationPacket(String license, String companyName, String[] containerIds, String[] containerContents) {
        this.license = license;
        this.companyName = companyName;
        this.containerIds = containerIds;
        this.containerContents = containerContents;
    }

    public String getLicense() {
        return license;
    }

    public List<String> getContainerIds() {
        return Arrays.asList(containerIds);
    }

    public List<String> getContainerContents() {
        return Arrays.asList(containerContents);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
