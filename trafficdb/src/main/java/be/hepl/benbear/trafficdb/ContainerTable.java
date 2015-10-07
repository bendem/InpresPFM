package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.SimpleTable;

public class ContainerTable extends SimpleTable<Container, String> {

    public ContainerTable() {
        super(
            "containers",
            r -> new Container(
                r.getString("container_id"),
                r.getInt("company_id"),
                r.getString("content_type"),
                r.getString("dangers")
            ),
            "container_id"
        );
    }

}
