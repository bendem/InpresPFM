package be.hepl.benbear.decisiondb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("anova_stats")
public class AnovaStats {
    @PrimaryKey
    private final int id;
    private final int ssample_size;
    private final double spvalue;
    private final int sresult;

    public AnovaStats(int id, int ssample_size, double spvalue, int sresult) {
        this.id = id;
        this.ssample_size = ssample_size;
        this.spvalue = spvalue;
        this.sresult = sresult;
    }

    public int getId() {
        return id;
    }

    public int getSsample_size() {
        return ssample_size;
    }

    public double getSpvalue() {
        return spvalue;
    }

    public int getSresult() {
        return sresult;
    }
}
