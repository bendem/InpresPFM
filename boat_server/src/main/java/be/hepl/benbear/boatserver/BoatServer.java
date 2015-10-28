package be.hepl.benbear.boatserver;

import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.iobrep.LoginPacket;
import be.hepl.benbear.iobrep.LoginResponsePacket;
import be.hepl.benbear.iobrep.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public class BoatServer extends Server<ObjectInputStream, ObjectOutputStream> {

    private final Set<UUID> sessions;

    public BoatServer(int port, ExecutorService threadPool) {
        super(
            port,
            Thread::new,
            threadPool,
            UncheckedLambda.function(ObjectInputStream::new, Throwable::printStackTrace),
            UncheckedLambda.function(ObjectOutputStream::new, Throwable::printStackTrace)
        );
        this.sessions = new CopyOnWriteArraySet<>();
    }

    @Override
    protected void read(ObjectInputStream is, ObjectOutputStream os) throws IOException {
        Object o;
        try {
            o = is.readObject();
        } catch(ClassNotFoundException e) {
            return;
        }

        switch(((Packet) o).getId()) {
            case LOGIN:
                handleLogin((LoginPacket) o, os);
                break;
            case GET_CONTAINERS:
                break;
            case CONTAINER_OUT:
                break;
            case CONTAINER_OUT_END:
                break;
            case BOAT_ARRIVED:
                break;
            case CONTAINER_IN:
                break;
            case CONTAINER_IN_END:
                break;
            default:
                System.err.println("unhandled packet: " + ((Packet) o).getId().name() + " (" + o.getClass().getName() + ")");
        }
        os.flush();
    }

    private void handleLogin(LoginPacket packet, ObjectOutputStream os) throws IOException {
        System.out.printf("%s:%s%n", packet.getUsername(), packet.getPassword());
        UUID session = UUID.randomUUID();
        sessions.add(session);
        os.writeObject(new LoginResponsePacket(session, null));
    }

    @Override
    protected void onClose(Socket socket, Exception e) {
        e.printStackTrace();
    }

}
