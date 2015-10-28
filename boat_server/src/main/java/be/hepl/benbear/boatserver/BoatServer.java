package be.hepl.benbear.boatserver;

import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.commons.db.csv.CSVDatabase;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.iobrep.LoginPacket;
import be.hepl.benbear.iobrep.LoginResponsePacket;
import be.hepl.benbear.iobrep.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BoatServer extends Server<ObjectInputStream, ObjectOutputStream> {

    public static final String DATA_DIR = "data";

    private final Database accounting;
    private final Database containers;
    private final ReadWriteLock containersLock;
    private final Set<UUID> sessions;

    public BoatServer(int port, ExecutorService threadPool) {
        super(
            port,
            Thread::new,
            threadPool,
            UncheckedLambda.function(ObjectInputStream::new, Throwable::printStackTrace),
            UncheckedLambda.function(ObjectOutputStream::new, Throwable::printStackTrace)
        );

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.accounting = new SQLDatabase();
        this.accounting.registerClass(Staff.class);
        this.accounting.connect("jdbc:oracle:thin:@178.32.41.4:8080:xe", "accounting", "bleh");

        if(!Files.isDirectory(Paths.get(DATA_DIR))) {
            try {
                Files.createDirectory(Paths.get(DATA_DIR));
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.containers = new CSVDatabase();
        this.containers.connect(DATA_DIR, null, null);
        this.containers.registerClass(CSVContainer.class);
        this.containersLock = new ReentrantReadWriteLock();
        Table<CSVContainer> table = containers.table(CSVContainer.class);
        table.insert(new CSVContainer(1, 2, "id", "destination", Instant.now()));
        try {
            CSVContainer c = table.findOne(DBPredicate.of("x", 1)).get().get();
            System.out.println(String.valueOf(c.getX()) + ':' + c.getY() + ':' + c.getId() + ':' + c.getDestination() + ':' + c.getArrival());
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

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
        Optional<Staff> user;
        try {
            user = accounting
                .table(Staff.class)
                .findOne(DBPredicate.of("login", packet.getUsername()))
                .get(5, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch(ExecutionException | TimeoutException e) {
            os.writeObject(new LoginResponsePacket(null, "An error happened: " + e.getMessage()));
            return;
        }

        if(!user.isPresent() || !user.get().getPassword().equals(packet.getPassword())) {
            os.writeObject(new LoginResponsePacket(null, "Unknown login or password"));
            return;
        }

        UUID session = UUID.randomUUID();
        sessions.add(session);
        os.writeObject(new LoginResponsePacket(session, null));
    }

    @Override
    protected void onClose(Socket socket, Exception e) {
        e.printStackTrace();
    }

}
