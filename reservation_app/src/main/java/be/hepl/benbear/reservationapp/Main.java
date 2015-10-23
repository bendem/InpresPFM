package be.hepl.benbear.reservationapp;

import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.trafficdb.Company;
import be.hepl.benbear.trafficdb.Container;
import be.hepl.benbear.trafficdb.Destination;
import be.hepl.benbear.trafficdb.Movement;
import be.hepl.benbear.trafficdb.Parc;
import be.hepl.benbear.trafficdb.Transporter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Main {

    private final Database database;

    public Main(Database db) throws Exception {
        database = db;
        database.registerClass(Company.class);
        database.registerClass(Container.class);
        database.registerClass(Destination.class);
        database.registerClass(Movement.class);
        database.registerClass(Parc.class);
        database.registerClass(Transporter.class);

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        database.connect("jdbc:oracle:thin:@localhost:1520:xe", "dbtraffic", "bleh");

        // ------
        Table<Company> table = database.table(Company.class);
        Optional<Company> comp = table.byId(1).get(5, TimeUnit.SECONDS);
        if(comp.isPresent()) {
            System.out.println(comp.get());
        }
        table.insert(new Company(0, "bleh", "mail", "phone", "bestaddress")).get();

        table.find().get().map(Company::getCompanyId).forEach(System.out::println);
        // ------
    }

    public static void main(String[] args) throws Exception {
        // TODO Arg parsing
        try(Database db = new Database()) {
            new Main(db);
        }
    }

}
