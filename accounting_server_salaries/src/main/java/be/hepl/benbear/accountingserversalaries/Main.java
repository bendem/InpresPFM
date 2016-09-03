package be.hepl.benbear.accountingserversalaries;

import be.hepl.benbear.accounting_db.Bill;
import be.hepl.benbear.accounting_db.BillItem;
import be.hepl.benbear.accounting_db.Bonus;
import be.hepl.benbear.accounting_db.Price;
import be.hepl.benbear.accounting_db.Salary;
import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;

import java.nio.file.Paths;

public class Main {

    public static void main(String... args) throws Exception {
        Config conf = new Config(args.length == 0 ? Paths.get("..", "global.conf") : Paths.get(args[0]));

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

        new SalariesServer(conf, accounting).start();
    }

}
