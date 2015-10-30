package be.hepl.benbear.boatserver;

import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.commons.db.csv.CSVDatabase;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.iobrep.*;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoatServer extends Server<ObjectInputStream, ObjectOutputStream> {

    public static final String DATA_DIR = "data";

    private final Database accounting;
    private final Table<Staff> accountTable;
    private final Database containers;
    private final ReadWriteLock containersLock;
    private final Table<CSVContainer> containerTable;
    private final Map<UUID, String> sessions;
    private final Map<UUID, Set<String>> containerLeaving;
    private final Map<UUID, Set<Container>> containerIncoming;
    private final Map<UUID, String> lockedDestinations;
    private final BufferedWriter boatWriter;

    public BoatServer(int port, ExecutorService threadPool) {
        super(
            port,
            Thread::new,
            threadPool,
            UncheckedLambda.function(ObjectInputStream::new, Throwable::printStackTrace),
            UncheckedLambda.function(os -> {
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.flush();
                return oos;
            }, Throwable::printStackTrace)
        );

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.accounting = new SQLDatabase();
        this.accounting.registerClass(Staff.class);
        this.accountTable = this.accounting.table(Staff.class);
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
        this.containerTable = this.containers.table(CSVContainer.class);
        this.containersLock = new ReentrantReadWriteLock();

        this.sessions = new ConcurrentHashMap<>();
        this.containerLeaving = new ConcurrentHashMap<>();
        this.containerIncoming = new ConcurrentHashMap<>();
        this.lockedDestinations = new ConcurrentHashMap<>();

        try {
            this.boatWriter = Files.newBufferedWriter(
                Paths.get(DATA_DIR).resolve("boats.csv"),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void read(ObjectInputStream is, ObjectOutputStream os) throws IOException {
        Object o;
        try {
            o = is.readObject();
        } catch(ClassNotFoundException e) {
            return;
        }

        Packet packet = (Packet) o;
        AuthenticatedPacket authenticatedPacket = null;
        if(packet instanceof AuthenticatedPacket) {
            authenticatedPacket = (AuthenticatedPacket) packet;
            UUID session = authenticatedPacket.getSession();
            if(session == null || !sessions.containsKey(session)) {
                System.out.printf("Received invalid session: %s%n", session);
                os.writeObject(new InvalidSessionResponsePacket(authenticatedPacket));
                return;
            }
        }

        System.out.printf("Handling packet %s from session %s%n",
            packet.getClass().getName(),
            authenticatedPacket == null ? null : authenticatedPacket.getSession());

        switch(packet.getId()) {
            case LOGIN:
                handleLogin((LoginPacket) o, os);
                break;
            case GET_CONTAINERS:
                handleGetContainers((GetContainersPacket) o, os);
                break;
            case CONTAINER_OUT:
                handleContainerOut((ContainerOutPacket) o, os);
                break;
            case CONTAINER_OUT_END:
                handleContainerOutEnd((ContainerOutEndPacket) o, os);
                break;
            case BOAT_ARRIVED:
                handleBoatArrived((BoatArrivedPacket) o, os);
                break;
            case CONTAINER_IN:
                handleContainerIn((ContainerInPacket) o, os);
                break;
            case CONTAINER_IN_END:
                handleContainerInEnd((ContainerInEndPacket) o, os);
                break;
            default:
                System.err.println("unhandled packet: " + packet.getId().name() + " (" + o.getClass().getName() + ")");
        }
        os.flush();
    }

    private void handleLogin(LoginPacket p, ObjectOutputStream os) throws IOException {
        System.out.printf("%s tried to connect%n", p.getUsername());
        Optional<Staff> user;
        try {
            user = accountTable
                .findOne(DBPredicate.of("login", p.getUsername()))
                .get(5, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch(ExecutionException | TimeoutException e) {
            e.printStackTrace();
            os.writeObject(new LoginResponsePacket(null, "An error happened: " + e.getMessage()));
            return;
        }

        if(!user.isPresent() || !user.get().getPassword().equals(p.getPassword())) {
            os.writeObject(new LoginResponsePacket(null, "Unknown login or password"));
            System.out.printf("%s failed to connect%n", p.getUsername());
            return;
        }

        if(sessions.containsValue(p.getUsername())) {
            disconnect(p.getUsername());
        }

        UUID session = UUID.randomUUID();
        System.out.printf("%s connected %s%n", p.getUsername(), session);
        sessions.put(session, p.getUsername());
        os.writeObject(new LoginResponsePacket(session, null));
    }

    private void handleGetContainers(GetContainersPacket p, ObjectOutputStream os) throws IOException {
        containersLock.readLock().lock();
        try {
            if(lockedDestinations.containsValue(p.getDestination())) {
                os.writeObject(new GetContainersResponsePacket("A boat is already loading containers for that destination", null));
                return;
            }
            List<Container> containers;
            try {
                Stream<Container> containerStream = containerTable
                    .find(DBPredicate.of("destination", p.getDestination()))
                    .get()
                    .map(CSVContainer::toContainer);

                if(p.getCriteria() == Criteria.FIRST) {
                    containerStream = containerStream
                        .sorted((c1, c2) -> c1.getArrival().compareTo(c2.getArrival()));
                }

                containers = containerStream
                    .collect(Collectors.toList());
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch(ExecutionException e) {
                os.writeObject(new GetContainersResponsePacket("An error happened: " + e.getMessage(), null));
                return;
            }
            lockedDestinations.put(p.getSession(), p.getDestination());
            os.writeObject(new GetContainersResponsePacket(null, containers));
        } finally {
            containersLock.readLock().unlock();
        }
    }

    private void handleContainerOut(ContainerOutPacket p, ObjectOutputStream os) throws IOException {
        boolean added = containerLeaving
            .computeIfAbsent(p.getSession(), k -> Collections.synchronizedSet(new HashSet<>()))
            .add(p.getContainerId());
        if(added) {
            os.writeObject(new ContainerOutResponsePacket(p.getContainerId(), null));
        } else {
            os.writeObject(new ContainerOutResponsePacket(null, p.getContainerId() + " already out"));
        }
    }

    private void handleContainerOutEnd(ContainerOutEndPacket p, ObjectOutputStream os) throws IOException {
        containersLock.writeLock().lock();
        try {
            Set<String> ids = containerLeaving.get(p.getSession());
            containerLeaving.remove(p.getSession());
            lockedDestinations.remove(p.getSession());

            if(ids == null) {
                os.writeObject(new ContainerOutEndResponsePacket("No containers to remove"));
                return;
            }

            DBPredicate predicate = null;
            for(String id : ids) {
                if(predicate == null) {
                    predicate = DBPredicate.of("id", id);
                } else {
                    predicate.or("id", id);
                }
            }

            int count;
            try {
                count = containerTable.delete(predicate).get();
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch(ExecutionException e) {
                e.printStackTrace();
                os.writeObject(new ContainerOutEndResponsePacket("An error happened: " + e.getMessage()));
                return;
            }

            System.out.printf("Removed %d containers%n", count);
            os.writeObject(new ContainerOutEndResponsePacket(null));
        } finally {
            containersLock.writeLock().unlock();
        }
    }

    private synchronized void handleBoatArrived(BoatArrivedPacket p, ObjectOutputStream os) throws IOException {
        try {
            boatWriter.write(p.getBoatId() + ';' + p.getDestination());
            boatWriter.newLine();
        } catch(IOException e) {
            os.writeObject(new BoatArrivedResponsePacket("An error happened: " + e.getMessage()));
            return;
        }
        os.writeObject(new BoatArrivedResponsePacket(null));
    }

    private void handleContainerIn(ContainerInPacket p, ObjectOutputStream os) throws IOException {
        // TODO Search for a place in the next version
        boolean added = containerIncoming
            .computeIfAbsent(p.getSession(), k -> Collections.synchronizedSet(new HashSet<>()))
            .add(p.getContainer());
        if(added) {
            os.writeObject(new ContainerInResponsePacket(null, p.getContainer()));
        } else {
            os.writeObject(new ContainerInResponsePacket("Container already stored: " + p.getContainer().getId(), null));
        }
    }

    private void handleContainerInEnd(ContainerInEndPacket p, ObjectOutputStream os) throws IOException {
        containersLock.writeLock().lock();
        try {
            Set<Container> containers = containerIncoming.get(p.getSession());
            containerIncoming.remove(p.getSession());
            for(Container container : containers) {
                containerTable.insert(CSVContainer.fromContainer(container)).get();
            }
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch(ExecutionException e) {
            e.printStackTrace();
            os.writeObject(new ContainerInEndResponsePacket("An error happened: " + e.getMessage()));
            return;
        } finally {
            containersLock.writeLock().unlock();
        }
        os.writeObject(new ContainerInEndResponsePacket(null));
    }

    private void disconnect(String username) {
        Optional<UUID> optUuid = sessions.entrySet().stream()
            .filter(e -> e.getValue().equals(username))
            .map(Map.Entry::getKey)
            .findFirst();
        if(!optUuid.isPresent()) {
            return;
        }
        UUID uuid = optUuid.get();
        System.out.printf("Disconnecting %s from session %s%n", username, uuid);
        sessions.remove(uuid);
        containerLeaving.remove(uuid);
        containerIncoming.remove(uuid);
        lockedDestinations.remove(uuid);
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        if(e != null && !(e instanceof EOFException)) {
            e.printStackTrace();
        }
    }

}
