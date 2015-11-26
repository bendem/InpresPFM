package be.hepl.benbear.pidep;

import java.util.UUID;

public class GetStatInferHomogeneityTestPacket extends AuthenticatedPacket {

    private final int numberOfElem;

    public GetStatInferHomogeneityTestPacket(UUID session, int numberOfElem) {
        super(Id.GetStatInferHomogeneityTestPacket, session);
        this.numberOfElem = numberOfElem;
    }

    public int getNumberOfElem() {
        return numberOfElem;
    }
}
