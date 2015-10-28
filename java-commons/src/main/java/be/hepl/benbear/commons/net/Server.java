package be.hepl.benbear.commons.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

public class Server {

    private static final int MAX_TRIES = 3;

    private final ServerSocket socket;

    public Server(int port) {
        try {
            this.socket = ServerSocketFactory.getDefault().createServerSocket(port);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket accept() {
        return accept(0);
    }

    private Socket accept(int tries) {
        try {
            return socket.accept();
        } catch(IOException e) {
            if(tries < MAX_TRIES) {
                return accept(tries + 1);
            }
            throw new RuntimeException(e);
        }
    }

}
