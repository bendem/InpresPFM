package be.hepl.benbear.trafficserver;

import be.hepl.benbear.commons.config.Config;

import java.io.IOException;

public class Main {

    public static void main(String...args) throws IOException {
            new TrafficServer(new Config(args.length == 0 ? null : args[0])).start();
    }
}
