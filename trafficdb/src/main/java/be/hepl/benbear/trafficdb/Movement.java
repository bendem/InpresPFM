package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("movements")
public class Movement {

    @PrimaryKey
    private final int movementId;
    private final String containerId;
    private final int companyId;
    private final String transporterIdIn;
    private final String transporterIdOut;
    private final Date dateArrival;
    private final Date dateDeparture;
    private final int weight;
    private final int destinationId;

    public Movement(int movementId, String containerId, int companyId, String transporterIdIn, String transporterIdOut, Date dateArrival, Date dateDeparture, int weight, int destinationId) {
        this.movementId = movementId;
        this.containerId = containerId;
        this.companyId = companyId;
        this.transporterIdIn = transporterIdIn;
        this.transporterIdOut = transporterIdOut;
        this.dateArrival = dateArrival;
        this.dateDeparture = dateDeparture;
        this.weight = weight;
        this.destinationId = destinationId;
    }

    public int getMovementId() {
        return movementId;
    }

    public String getContainerId() {
        return containerId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getTransporterIdIn() {
        return transporterIdIn;
    }

    public String getTransporterIdOut() {
        return transporterIdOut;
    }

    public Date getDateArrival() {
        return dateArrival;
    }

    public Date getDateDeparture() {
        return dateDeparture;
    }

    public int getWeight() {
        return weight;
    }

    public int getDestinationId() {
        return destinationId;
    }

}
