package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;

@DBTable("container_per_dest_month")
public class ContainerPerDestMonth implements ContainterPerDestination {
    private final int count;
    private final String city;
    private final int month;

    public ContainerPerDestMonth(int count, String city, int month) {
        this.count = count;
        this.city = city;
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public String getCity() {
        return city;
    }

    public int getCount() {
        return count;
    }
}
