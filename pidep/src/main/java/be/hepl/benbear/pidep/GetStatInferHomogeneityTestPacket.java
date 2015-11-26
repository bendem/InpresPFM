package be.hepl.benbear.pidep;

import java.util.UUID;

public class GetStatInferHomogeneityTestPacket extends AuthenticatedPacket {

    private final int numberOfElem;
    private final String firstCity;
    private final String secondCity;

    public GetStatInferHomogeneityTestPacket(UUID session, int numberOfElem, String firstCity, String secondCity) {
        super(Id.GetStatInferHomogeneityTest, session);
        this.numberOfElem = numberOfElem;
        this.firstCity = firstCity;
        this.secondCity = secondCity;
    }

    public int getNumberOfElem() {
        return numberOfElem;
    }

    public String getFirstCity() {
        return firstCity;
    }

    public String getSecondCity() {
        return secondCity;
    }
}
