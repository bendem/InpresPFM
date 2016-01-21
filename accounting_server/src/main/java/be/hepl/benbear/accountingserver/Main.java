package be.hepl.benbear.accountingserver;

import be.hepl.benbear.commons.config.Config;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String... args) throws IOException {
        new AccountingBillServer(new Config(args.length == 0 ? null : Paths.get(args[0]))).start();
    }

}
