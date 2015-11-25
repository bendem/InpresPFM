package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;

@DBTable("container_per_dest_quarter")
public class ContainerPerDestQuarter implements ContainterPerDestination {
    private final int count;
    private final String city;
    private final int year;
    private final int quarter;

    public ContainerPerDestQuarter(int count, String city, int year, int quarter) {
        this.count = count;
        this.city = city;
        this.year = year;
        this.quarter = quarter;
    }


    public String getCity() {
        return city;
    }

    public int getCount() {
        return count;
    }

    public int getYear() {
        return year;
    }

    public int getQuarter() {
        return quarter;
    }
}
