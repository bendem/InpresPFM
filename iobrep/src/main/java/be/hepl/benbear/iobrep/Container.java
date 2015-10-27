package be.hepl.benbear.iobrep;

import java.io.Serializable;
import java.util.Date;

public class Container implements Serializable {

    private final int x;
    private final int y;
    private final String id;
    private final String destination;
    private final Date arrival;

    public Container(int x, int y, String id, String destination, Date arrival) {
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

    public Date getArrival() {
        return arrival;
    }

}
