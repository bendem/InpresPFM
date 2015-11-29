package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class GetXYPacket implements Packet {
    public static final byte ID = 3;

    private final String company;
    private final String truckImmat;
    private final String destination;
    private final String[] containerIds;

    public GetXYPacket(String company, String truckImmat, String destination, String[] containerIds) {
        this.company = company;
        this.truckImmat = truckImmat;
        this.destination = destination;
        this.containerIds = containerIds;
    }


    @Override
    public byte getId() {
        return ID;
    }

    public String getCompany() {
        return company;
    }

    public String getTruckImmat() {
        return truckImmat;
    }

    public String getDestination() {
        return destination;
    }

    public List<String> getContainerIds() {
        return Arrays.asList(containerIds);
    }
}
