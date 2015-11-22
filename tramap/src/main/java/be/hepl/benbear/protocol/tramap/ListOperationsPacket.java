package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

import java.time.Instant;

public class ListOperationsPacket implements Packet {

    public static final byte ID = 5;

    public enum Type {
        Society, Destination;

    }

    private final long start;
    private final long end;
    private final String criteria;
    private final String type;

    public ListOperationsPacket(long start, long end, String str, String type) {
        this.start = start;
        this.end = end;
        this.criteria = str;
        this.type = type;
    }
    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getType() {
        return type;
    }


    @Override
    public byte getId() {
        return ID;
    }

}
