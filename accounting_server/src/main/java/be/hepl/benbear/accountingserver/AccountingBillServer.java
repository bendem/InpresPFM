package be.hepl.benbear.accountingserver;

import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.chamap.LoginPacket;
import be.hepl.benbear.chamap.LoginResponsePacket;
import be.hepl.benbear.chamap.MakeBillPacket;
import be.hepl.benbear.chamap.MakeBillResponsePacket;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolException;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.security.Digestion;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class AccountingBillServer extends Server<DataInputStream, DataOutputStream> {

    public static final Duration SESSION_TIMEOUT = Duration.ofHours(1);

    private final ProtocolHandler proto;
    private final Database accounting;
    private final Database traffic;
    private final Map<UUID, Instant> sessions;

    public AccountingBillServer(Config conf, Database accounting, Database traffic) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(conf.getStringThrowing("accounting.host"))).get(),
            conf.getIntThrowing("accounting_bill_server.port"),
            Thread::new,
            Executors.newSingleThreadExecutor(),
            DataInputStream::new,
            DataOutputStream::new
        );

        this.accounting = accounting;
        this.traffic = traffic;

        sessions = new ConcurrentHashMap<>();

        proto = new ProtocolHandler();
        proto
            .registerPacket((byte) 1, LoginPacket.class)
            .registerPacket((byte) 2, LoginResponsePacket.class)
            .registerPacket((byte) 3, MakeBillPacket.class)
            .registerPacket((byte) 4, MakeBillResponsePacket.class)
            ;
    }

    @Override
    protected void read(DataInputStream is, DataOutputStream os) throws IOException {
        Packet packet = proto.read(is);
        switch(packet.getId()) {
            case 1:
                handleLogin((LoginPacket) packet, os);
                break;
            case 2:
                handleMakeBill((MakeBillPacket) packet, os);
            default:
                throw new ProtocolException(String.format("Invalid id received: %s", packet.getId()));
        }
    }

    private void handleLogin(LoginPacket p, DataOutputStream os) throws IOException {
        Log.i("Handling login attempt");

        Optional<Staff> staff;
        try {
            staff = accounting.table(Staff.class).findOne(DBPredicate.of("login", p.getUsername())).get();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        } catch(ExecutionException e) {
            proto.write(os, LoginResponsePacket.ERROR);
            return;
        }

        if(!staff.isPresent()) {
            proto.write(os, LoginResponsePacket.ERROR);
            return;
        }

        if(Digestion.check(p.getDigest(), staff.get().getPassword(), p.getTime(), p.getSalt())) {
            UUID session = UUID.randomUUID();
            sessions.put(session, Instant.now());
            proto.write(os, new LoginResponsePacket(session));
        } else {
            proto.write(os, LoginResponsePacket.ERROR);
        }
    }

    private void handleMakeBill(MakeBillPacket p, DataOutputStream os) throws IOException {
        if(!isSessionValid(p.getSession())) {
            Log.w("Invalid MakeBillPacket session id");
            proto.write(os, new MakeBillResponsePacket("Not connected"));
            return;
        }

        Log.i("Making bills");

        // TODO
    }

    private boolean isSessionValid(UUID session) {
        Instant lastSeen = sessions.get(session);
        if(lastSeen == null) {
            return false;
        }

        Instant now = Instant.now();
        if(lastSeen.plus(SESSION_TIMEOUT).isBefore(now)) {
            sessions.remove(session);
            return false;
        }

        sessions.put(session, now);
        return true;
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        // TODO Possibly log errors?
    }

}
