package be.hepl.benbear.boatserver;

import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        int port = 31061;
        int threads = 2;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        if(args.length > 1) {
            threads = Integer.parseInt(args[1]);
        }

        new BoatServer(port, Executors.newFixedThreadPool(threads)).start();
    }

}
