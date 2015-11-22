package be.hepl.benbear.chatserver;

import be.hepl.benbear.commons.config.Config;

import java.nio.file.Paths;

public class Main {

    public static void main(String...args) {
        new ChatServer(new Config(args.length == 0 ? null : Paths.get(args[0]))).start();
    }

}
