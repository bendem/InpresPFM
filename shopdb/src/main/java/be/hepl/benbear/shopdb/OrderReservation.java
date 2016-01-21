package be.hepl.benbear.shopdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("order_reservation")
public class OrderReservation {

    @PrimaryKey
    private final int orderId;
    @PrimaryKey
    private final Date reservationDay;
    private final int numberPlace;

    public OrderReservation(int orderId, Date reservationDay, int numberPlace) {
        this.orderId = orderId;
        this.reservationDay = reservationDay;
        this.numberPlace = numberPlace;
    }

    public int getOrderId() {
        return orderId;
    }

    public Date getReservationDay() {
        return reservationDay;
    }

    public int getNumberPlace() {
        return numberPlace;
    }
}
