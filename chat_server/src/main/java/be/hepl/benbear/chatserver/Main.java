package be.hepl.benbear.chatserver;

public class Main {

    public static void main(String...args) {
        int port = 31063;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new ChatServer(port).start();
    }

}
