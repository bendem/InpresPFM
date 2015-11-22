package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("reservations_containers")
public class ReservationsContainers {
    @PrimaryKey
    private final String reservationId;
    @PrimaryKey
    private final int x;
    @PrimaryKey
    private final int y;
    private final String containerId;

    public ReservationsContainers(String reservationId, int x, int y, String containerId) {
        this.reservationId = reservationId;
        this.x = x;
        this.y = y;
        this.containerId = containerId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public String getContainerId() {
        return containerId;
    }
}
