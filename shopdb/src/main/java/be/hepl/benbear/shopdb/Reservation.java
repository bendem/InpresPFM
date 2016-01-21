package be.hepl.benbear.shopdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("reservations")
public class Reservation {

    @PrimaryKey
    private final Date reservationDay;
    private final int placeSold;

    public Reservation(Date reservationDay, int placeSold) {
        this.reservationDay = reservationDay;
        this.placeSold = placeSold;
    }

    public Date getReservationDay() {
        return reservationDay;
    }

    public int getPlaceSold() {
        return placeSold;
    }
}

