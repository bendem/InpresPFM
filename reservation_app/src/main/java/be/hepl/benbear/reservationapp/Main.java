package be.hepl.benbear.reservationapp;

import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.trafficdb.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Main {

    private final Database database;

    public Main() throws Exception {
        database = new Database();
        database.registerTable(Company.class, new CompanyTable());
        database.registerTable(Container.class, new ContainerTable());
        database.registerTable(Destination.class, new DestinationTable());
        database.registerTable(Movement.class, new MovementTable());
        database.registerTable(Parc.class, new ParcTable());
        database.registerTable(Transporter.class, new TransporterTable());

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        database.connect("jdbc:oracle:thin:@localhost:1520:xe", "dbtraffic", "bleh");

        // ------
        CompanyTable company = database.table("companies");
        Optional<Company> comp = company.byId(1, Throwable::printStackTrace).get(5, TimeUnit.SECONDS);
        if(comp.isPresent()) {
            System.out.println(comp.get());
        }
        // ------

        database.close();
    }

    public static void main(String[] args) throws Exception {
        // TODO Arg parsing
        new Main();
    }

}
