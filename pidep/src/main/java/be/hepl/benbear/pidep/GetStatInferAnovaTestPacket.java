package be.hepl.benbear.pidep;

import java.util.UUID;

public class GetStatInferAnovaTestPacket extends AuthenticatedPacket {

    private final int numberOfElem;

    public GetStatInferAnovaTestPacket(UUID session, int numberOfElem) {
        super(Id.GetStatInferAnovaTestPacket, session);
        this.numberOfElem = numberOfElem;
    }

    public int getNumberOfElem() {
        return numberOfElem;
    }
}
