package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("reservations")
public class Reservation {

    @PrimaryKey
    private final String destinationId;
    private final String reservationId;
    private final Date dateArrival;

    public Reservation(String reservationId, String destinationId, Date dateArrival) {
        this.reservationId = reservationId;
        this.destinationId = destinationId;
        this.dateArrival = dateArrival;
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
