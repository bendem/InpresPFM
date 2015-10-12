package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("reservations")
public class Reservation {

    @PrimaryKey
    private final int x;
    @PrimaryKey
    private final int y;
    @PrimaryKey
    private final Date dateArrival;
    private final String destinationId;
    private final String reservationId;

    public Reservation(int x, int y, Date dateArrival, String destinationId, String reservationId) {
        this.x = x;
        this.y = y;
        this.dateArrival = dateArrival;
        this.destinationId = destinationId;
        this.reservationId = reservationId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Date getDateArrival() {
        return dateArrival;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getReservationId() {
        return reservationId;
    }
}
