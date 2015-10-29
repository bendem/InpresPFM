package be.hepl.benbear.boatserver;

public class Boat {

    private final String id;
    private final String destination;

    public Boat(String id, String destination) {
        this.id = id;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

}
