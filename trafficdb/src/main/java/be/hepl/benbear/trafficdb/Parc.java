package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("parcs")
public class Parc {

    @PrimaryKey
    private final int x;
    @PrimaryKey
    private final int y;
    private final String containerId;

    public Parc(int x, int y, String containerId) {
        this.x = x;
        this.y = y;
        this.containerId = containerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getContainerId() {
        return containerId;
    }

}
