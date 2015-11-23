package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("movements_light")
public class MovementsLight {
    @PrimaryKey
    private final int movementId;
    private final String containerId;
    private final String name;
    private final String city;
    private final Date dateArrival;
    private final Date dateDeparture;

    public MovementsLight(int movementId, String containerId, String name, String city, Date dateArrival, Date dateDeparture) {
        this.movementId = movementId;
        this.containerId = containerId;
        this.name = name;
        this.city = city;
        this.dateArrival = dateArrival;
        this.dateDeparture = dateDeparture;
    }

    public int getMovementId() {
        return movementId;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getDestination() {
        return city;
    }

    public String getCompanyName() {
        return name;
    }

    public Date getDateArrival() {
        return dateArrival;
    }

    public Date getDateDeparture() {
        return dateDeparture;
    }
}
