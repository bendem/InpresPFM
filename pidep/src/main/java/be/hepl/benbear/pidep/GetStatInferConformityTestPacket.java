package be.hepl.benbear.pidep;

import java.util.UUID;

public class GetStatInferConformityTestPacket extends AuthenticatedPacket {

    private final int numberOfElem;

    public GetStatInferConformityTestPacket(UUID session, int numberOfElem) {
        super(Id.GetStatInferConformityTestPacket, session);
        this.numberOfElem = numberOfElem;
    }

    public int getNumberOfElem() {
        return numberOfElem;
    }
}
