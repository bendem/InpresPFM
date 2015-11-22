package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("free_empl")
public class FreeParc {
    @PrimaryKey
    private final int x;
    @PrimaryKey
    private final int y;

    public FreeParc(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
