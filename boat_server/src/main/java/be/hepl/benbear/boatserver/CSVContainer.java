package be.hepl.benbear.boatserver;

import be.hepl.benbear.commons.db.DBTable;

import java.time.Instant;

@DBTable("containers")
public class CSVContainer {

    private final int x;
    private final int y;
    private final String id;
    private final String destination;
    private final Instant arrival;

    public CSVContainer(int x, int y, String id, String destination, Instant arrival) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.destination = destination;
        this.arrival = arrival;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public Instant getArrival() {
        return arrival;
    }

}
