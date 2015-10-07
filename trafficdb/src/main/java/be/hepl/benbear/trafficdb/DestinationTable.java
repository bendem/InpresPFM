package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.SimpleTable;

public class DestinationTable extends SimpleTable<Destination, Integer> {

    public DestinationTable() {
        super(
            "destinations",
            r -> new Destination(
                r.getInt("destination_id"),
                r.getString("city"),
                r.getInt("distance_road"),
                r.getInt("distance_boat"),
                r.getInt("distance_train")
            ),
            "destination_id"
        );
    }

}
