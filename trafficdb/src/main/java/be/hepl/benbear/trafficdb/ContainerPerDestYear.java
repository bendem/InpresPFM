package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;

@DBTable("container_per_dest_year")
public class ContainerPerDestYear implements ContainterPerDestination {
    private final int count;
    private final String city;
    private final int year;

    public ContainerPerDestYear(int count, String city, int year) {
        this.count = count;
        this.city = city;
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public String getCity() {
        return city;
    }

    public int getCount() {
        return count;
    }
}
