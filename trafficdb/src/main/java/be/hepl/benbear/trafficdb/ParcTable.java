package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.CompositeIdTable;

public class ParcTable extends CompositeIdTable<Parc, Integer, Integer> {

    public ParcTable() {
        super(
            "parcs",
            r -> new Parc(r.getInt("x"), r.getInt("y"), r.getString("container_id")),
            "x",
            "y"
        );
    }

}
