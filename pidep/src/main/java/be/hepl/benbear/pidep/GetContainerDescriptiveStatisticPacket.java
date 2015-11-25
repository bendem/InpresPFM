package be.hepl.benbear.pidep;

import java.util.UUID;

public class GetContainerDescriptiveStatisticPacket extends AuthenticatedPacket {

    public enum Type {
        IN, OUT
    }

    private final int sampleSize;
    private final Type type;

    public GetContainerDescriptiveStatisticPacket(UUID session, int sampleSize, Type type) {
        super(Id.GetContainerDescriptiveStatistic, session);
        this.sampleSize = sampleSize;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public int getSampleSize() {
        return sampleSize;
    }

}
