package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.protocol.Packet;

public class SendWeightPacket implements Packet {
    public static final byte ID = 7;

    private final String[] containerIds;
    private final Position[] containerPositions;
    private final int[] containerWeights;

    public SendWeightPacket(String[] containerIds, Position[] containerPositions, int[] containerWeights) {
        this.containerIds = containerIds;
        this.containerPositions = containerPositions;
        this.containerWeights = containerWeights;
    }

    public String[] getContainerIds() {
        return containerIds;
    }

    public Position[] getContainerPositions() {
        return containerPositions;
    }

    public int[] getContainerWeights() {
        return containerWeights;
    }

    @Override
    public byte getId() {
        return ID;
    }
}
