package be.hepl.benbear.boatserver;

import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.iobrep.AuthenticatedPacket;
import be.hepl.benbear.iobrep.LoginResponsePacket;
import be.hepl.benbear.iobrep.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public class BoatServer {

    private final Server server;
    private final ExecutorService executorService;
    private final Set<UUID> sessions;
    private volatile boolean closed = false;

    public BoatServer(int port, ExecutorService executorService) {
        this.server = new Server(port);
        this.executorService = executorService;
        this.sessions = new CopyOnWriteArraySet<>();
    }

    public void start() {
        while(!closed) {
            Socket socket = server.accept();
            executorService.submit(() -> {
                try(ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                    while(!Thread.interrupted()) {
                        Object o = ois.readObject();
                        if(!(o instanceof Packet)) {
                            continue;
                        }
                        Packet p = (Packet) o;
                        if(p instanceof AuthenticatedPacket) {
                            if(!sessions.contains(((AuthenticatedPacket) p).getSession())) {
                                oos.writeObject(new LoginResponsePacket(null, "Invalid session"));
                            }
                        }
                    }
                }

                // Infers to Callable and allows to ignore exceptions
                return null;
            });
        }
    }

}
