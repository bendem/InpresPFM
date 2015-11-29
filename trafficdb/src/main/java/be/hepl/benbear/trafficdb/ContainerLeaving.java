package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("container_leaving")
public class ContainerLeaving {
    @PrimaryKey
    private final int movementId;
    private final String containerId;
    private final String name;
    private final String city;
    private final Date dateArrival;
    private final double weight;
    private final int x;
    private final int y;

    public ContainerLeaving(int movementId, String containerId, String name, String city, Date dateArrival, double weight, int x, int y) {
        this.movementId = movementId;
        this.containerId = containerId;
        this.name = name;
        this.city = city;
        this.dateArrival = dateArrival;
        this.weight = weight;
        this.x = x;
        this.y = y;
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

    public double getWeight() {
        return weight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

