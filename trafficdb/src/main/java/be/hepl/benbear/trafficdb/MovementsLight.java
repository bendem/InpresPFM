package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("movements_light")
public class MovementsLight {
    @PrimaryKey
    private final int movementId;
    private final String containerId;
    private final String destination;
    private final String company_name;
    private final Date dateArrival;
    private final Date dateDeparture;

    public MovementsLight(int movementId, String containerId, String destination, String company_name, Date dateArrival, Date dateDeparture) {
        this.movementId = movementId;
        this.containerId = containerId;
        this.destination = destination;
        this.company_name = company_name;
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
        return destination;
    }

    public String getCompany_name() {
        return company_name;
    }

    public Date getDateArrival() {
        return dateArrival;
    }

    public Date getDateDeparture() {
        return dateDeparture;
    }
}
