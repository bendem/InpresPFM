package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.SimpleTable;

public class TransporterTable extends SimpleTable<Transporter, String> {

    public TransporterTable() {
        super(
            "transporters",
            r -> new Transporter(
                r.getString("transporter_id"),
                r.getInt("company_id"),
                r.getString("info")
            ),
            "transporter_id"
        );
    }

}
