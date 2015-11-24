package be.hepl.benbear.dataanalysisserver;

import be.hepl.benbear.commons.config.Config;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        new DataAnalysisServer(new Config(args.length == 0 ? null : args[0]));
    }

}
