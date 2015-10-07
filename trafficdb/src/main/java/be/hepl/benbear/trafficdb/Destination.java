package be.hepl.benbear.trafficdb;

public class Destination {

    private final int destinationId;
    private final String city;
    private final int distanceRoad;
    private final int distanceBoat;
    private final int distanceTrain;

    public Destination(int destinationId, String city, int distanceRoad, int distanceBoat, int distanceTrain) {
        this.destinationId = destinationId;
        this.city = city;
        this.distanceRoad = distanceRoad;
        this.distanceBoat = distanceBoat;
        this.distanceTrain = distanceTrain;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public String getCity() {
        return city;
    }

    public int getDistanceRoad() {
        return distanceRoad;
    }

    public int getDistanceBoat() {
        return distanceBoat;
    }

    public int getDistanceTrain() {
        return distanceTrain;
    }

}
