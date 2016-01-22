package be.hepl.benbear.accountingserver;

import be.hepl.benbear.accounting_db.Bill;
import be.hepl.benbear.accounting_db.BillItem;
import be.hepl.benbear.accounting_db.Bonus;
import be.hepl.benbear.accounting_db.Price;
import be.hepl.benbear.accounting_db.Salary;
import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.trafficdb.*;

import java.nio.file.Paths;

public class Main {

    public static void main(String... args) throws Exception {
        Config conf = new Config(args.length == 0 ? null : Paths.get(args[0]));

        Database.Driver.ORACLE.load();

        Database accounting = new SQLDatabase()
            .registerClass(
                Bill.class,
                BillItem.class,
                Bonus.class,
                Price.class,
                Salary.class,
                Staff.class)
            .connect(
                conf.getStringThrowing("jdbc.url"),
                conf.getStringThrowing("jdbc.accounting.user"),
                conf.getStringThrowing("jdbc.accounting.password"));

        Database traffic = new SQLDatabase()
            .registerClass(
                User.class,
                Company.class,
                Container.class,
                Parc.class,
                Destination.class,
                Reservation.class,
                ReservationsContainers.class,
                Transporter.class,
                Movement.class)
            .connect(
                conf.getStringThrowing("jdbc.url"),
                conf.getStringThrowing("jdbc.trafficdb.user"),
                conf.getStringThrowing("jdbc.trafficdb.password"));

        new AccountingBillServer(conf, accounting, traffic).start();
        new AccountingServer(conf, accounting, traffic).start();
    }

}
