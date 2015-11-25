package be.hepl.benbear.dataanalysisserver;

import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.pidep.LoginPacket;
import be.hepl.benbear.pidep.LoginReponsePacket;
import be.hepl.benbear.pidep.Packet;
import be.hepl.benbear.trafficdb.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class DataAnalysisServer extends Server<ObjectInputStream, ObjectOutputStream> {

    private static final MessageDigest MESSAGE_DIGEST;
    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance("sha-1");
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private static synchronized byte[] digest(ByteBuffer bb) {
        MESSAGE_DIGEST.reset();
        MESSAGE_DIGEST.update(bb);
        return MESSAGE_DIGEST.digest();
    }

    private final SQLDatabase accountingDb;
    private final SQLDatabase trafficDb;
    private final Set<UUID> sessions;

    public DataAnalysisServer(Config config) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(config.getString("dataanalysisserver.host").orElse("localhost"))).get(),
            config.getInt("dataanalysisserver.port").orElse(31067),
            Thread::new,
            Executors.newFixedThreadPool(2),
            UncheckedLambda.function(ObjectInputStream::new),
            UncheckedLambda.function(os -> {
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.flush();
                return oos;
            })
        );

        Database.Driver.ORACLE.load();

        accountingDb = new SQLDatabase();
        accountingDb.registerClass(Staff.class);
        accountingDb.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.accounting.user").get(),
            config.getString("jdbc.accounting.password").get());

        trafficDb = new SQLDatabase();
        trafficDb.registerClass(Company.class,
            Container.class,
            Destination.class,
            FreeParc.class,
            Movement.class,
            MovementsLight.class,
            Parc.class,
            Reservation.class,
            ReservationsContainers.class,
            Transporter.class,
            User.class);
        trafficDb.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.trafficdb.user").get(),
            config.getString("jdbc.trafficdb.password").get());

        sessions = new CopyOnWriteArraySet<>();
    }

    @Override
    protected void read(ObjectInputStream is, ObjectOutputStream os) throws IOException {
        Packet packet;

        try {
            packet = (Packet) is.readObject();
        } catch(ClassNotFoundException | ClassCastException e) {
            Log.e("Invalid packet received", e);
            return;
        }

        switch(packet.getId()) {
            case Login:
                login(os, (LoginPacket) packet);
                break;
            case GetContainerDescriptiveStatistic:
            case GetContainerPerDestinationGraph:
            case GetContainerPerDestinationPerQuarterGraph:
            default:
                Log.e("Unhandled packet: %s", packet);
        }
        os.flush();
    }

    private void login(ObjectOutputStream os, LoginPacket p) throws IOException {
        Optional<Staff> user;
        try {
            user = accountingDb.table(Staff.class)
                .findOne(DBPredicate.of("login", p.getUsername())).get();
        } catch(InterruptedException | ExecutionException e) {
            Log.e("Failed to retrieve user %s", e, p.getUsername());
            os.writeObject(new LoginReponsePacket(null, "Internal error"));
            return;
        }

        if(!user.isPresent()) {
            os.writeObject(new LoginReponsePacket(null, "Unknown user"));
            return;
        }

        byte[] pwdBytes = user.get().getPassword().getBytes();
        ByteBuffer bb = ByteBuffer.allocate(p.getSalt().length + pwdBytes.length);
        bb.put(p.getSalt()).put(pwdBytes).flip();

        if(Arrays.equals(p.getDigest(), digest(bb))) {
            UUID uuid = UUID.randomUUID();
            sessions.add(uuid);
            os.writeObject(new LoginReponsePacket(uuid, null));
        } else {
            os.writeObject(new LoginReponsePacket(null, "Password invalid"));
        }
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        if(e != null) {
            Log.e("%s errored", e, channel);
        }
    }
}
