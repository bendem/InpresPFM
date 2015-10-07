package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.SimpleTable;

public class MovementTable extends SimpleTable<Movement, Integer> {

    public MovementTable() {
        super(
            "movements",
            r -> new Movement(
                r.getInt("movement_id"),
                r.getString("container_id"),
                r.getInt("company_id"),
                r.getString("transporter_id_in"),
                r.getString("transporter_id_out"),
                r.getDate("date_arrival"),
                r.getDate("date_departure"),
                r.getInt("weight"),
                r.getInt("destination_id")
            ),
            "movement_id"
        );
    }

}
