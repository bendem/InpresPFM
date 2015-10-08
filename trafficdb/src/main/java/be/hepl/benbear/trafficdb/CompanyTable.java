package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.SimpleTable;

public class CompanyTable extends SimpleTable<Company, Integer> {

    public CompanyTable() {
        super(
            "companies",
            r -> new Company(
                r.getInt("company_id"),
                r.getString("name"),
                r.getString("mail"),
                r.getString("phone"),
                r.getString("address")
            ),
            "company_id"
        );
    }

}
