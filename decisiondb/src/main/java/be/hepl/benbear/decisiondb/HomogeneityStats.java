package be.hepl.benbear.decisiondb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("homogeneity_stats")
public class HomogeneityStats {
    @PrimaryKey
    private final int id;
    private final int ssample_size;
    private final String sdestination1;
    private final String sdestination2;
    private final double spvalue;
    private final int sresult;

    public HomogeneityStats(int id, int ssample_size, String sdestination1, String sdestination2, double spvalue, int sresult) {
        this.id = id;
        this.ssample_size = ssample_size;
        this.sdestination1 = sdestination1;
        this.sdestination2 = sdestination2;
        this.spvalue = spvalue;
        this.sresult = sresult;
    }

    public int getId() {
        return id;
    }

    public int getSsample_size() {
        return ssample_size;
    }

    public String getSdestination1() {
        return sdestination1;
    }

    public String getSdestination2() {
        return sdestination2;
    }

    public double getSpvalue() {
        return spvalue;
    }

    public int getSresult() {
        return sresult;
    }
}
