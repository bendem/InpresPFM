package be.hepl.benbear.decisiondb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("descriptive_stats")
public class DescriptiveStats {
    @PrimaryKey
    private final int id;
    private final double smean;
    private final String smode;
    private final double smedian;
    private final double sstddev;
    private final String stypemov;
    private final double ssample_size;

    public DescriptiveStats(int id, double smean, String smode, double smedian, double sstddev, String stypemov, double ssample_size) {
        this.id = id;
        this.smean = smean;
        this.smode = smode;
        this.smedian = smedian;
        this.sstddev = sstddev;
        this.stypemov = stypemov;
        this.ssample_size = ssample_size;
    }

    public int getId() {
        return id;
    }

    public double getSmean() {
        return smean;
    }

    public String getSmode() {
        return smode;
    }

    public double getSmedian() {
        return smedian;
    }

    public double getSstddev() {
        return sstddev;
    }

    public String getStypemov() {
        return stypemov;
    }

    public double getSsample_size() {
        return ssample_size;
    }
}
