package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;



@DBTable("container_incoming")
public class ContainerIncoming {
    @PrimaryKey
    private final String containerId;
    private final String city;
    private final int x;
    private final int y;

    public ContainerIncoming(String containerId, String city, int x, int y) {
        this.containerId = containerId;
        this.city = city;
        this.x = x;
        this.y = y;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getCity() {
        return city;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
