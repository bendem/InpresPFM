package be.hepl.benbear.accountingserver;

import be.hepl.benbear.accounting_db.Bill;
import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.bisamap.*;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.commons.generics.Tuple3;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.security.Cipheriscope;
import be.hepl.benbear.commons.security.Digestion;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AccountingServer extends Server<InputStream, OutputStream> {

    public static final Duration SESSION_TIMEOUT = Duration.ofHours(1);
    public static final Duration TIME_TOLERANCE = Duration.ofMinutes(1);

    private final Config conf;
    //                          v last seen, sign key, crypt key v
    private final Map<UUID, Tuple3<Instant, SecretKey, SecretKey>> sessions;
    private final Database accounting;
    private final Database traffic;
    private final ProtocolHandler proto;

    public AccountingServer(Config conf, Database accounting, Database traffic) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(conf.getStringThrowing("accounting.host"))).get(),
            conf.getIntThrowing("accounting_server.port"),
            Thread::new,
            Executors.newSingleThreadExecutor(),
            Function.identity(),
            Function.identity()
        );
        this.conf = conf;
        this.sessions = new ConcurrentHashMap<>();
        this.accounting = accounting;
        this.traffic = traffic;

        proto = new ProtocolHandler()
            .registerPacket(PacketId.LoginPacket.id, LoginPacket.class)
            .registerPacket(PacketId.LoginResponsePacket.id, LoginResponsePacket.class)
            .registerPacket(PacketId.GetNextBillPacket.id, GetNextBillPacket.class)
            .registerPacket(PacketId.GetNextBillResponsePacket.id, GetNextBillResponsePacket.class)
            .registerPacket(PacketId.ValidateBillPacket.id, ValidateBillPacket.class)
            .registerPacket(PacketId.ValidateBillResponsePacket.id, ValidateBillResponsePacket.class)
            .registerPacket(PacketId.ListBillsPacket.id, ListBillsPacket.class)
            .registerPacket(PacketId.ListBillsResponsePacket.id, ListBillsResponsePacket.class)
            .registerPacket(PacketId.SendBillsPacket.id, SendBillsPacket.class)
            .registerPacket(PacketId.SendBillsResponsePacket.id, SendBillsResponsePacket.class)
            .registerPacket(PacketId.RecPayPacket.id, RecPayPacket.class)
            .registerPacket(PacketId.RecPayResponsePacket.id, RecPayResponsePacket.class)
            .registerPacket(PacketId.ListWaitingPacket.id, ListWaitingPacket.class)
            .registerPacket(PacketId.ListWaitingResponsePacket.id, ListWaitingResponsePacket.class)
            .registerPacket(PacketId.ComputeSalariesPacket.id, ComputeSalariesPacket.class)
            .registerPacket(PacketId.ComputeSalariesResponsePacket.id, ComputeSalariesResponsePacket.class)
            .registerPacket(PacketId.ValidateSalariesPacket.id, ValidateSalariesPacket.class)
            .registerPacket(PacketId.ValidateSalariesResponsePacket.id, ValidateSalariesResponsePacket.class)
            ;
    }

    @Override
    protected void read(InputStream is, OutputStream os) throws IOException {
        Packet packet = proto.read(is);
        PacketId id = PacketId.values()[packet.getId()];

        switch(id) {
            case LoginPacket:
                handleLogin((LoginPacket) packet, os);
                break;
            case GetNextBillPacket:
                handleGetNextBill((GetNextBillPacket) packet, os);
                break;
            case ValidateBillPacket:
                handleValidateBill((ValidateBillPacket) packet, os);
                break;
            case ListBillsPacket:
                handleListBills((ListBillsPacket) packet, os);
                break;
            case SendBillsPacket:
                handleSendBills((SendBillsPacket) packet, os);
                break;
            case RecPayPacket:
                handleRecPay((RecPayPacket) packet, os);
                break;
            case ListWaitingPacket:
                handleListWaiting((ListWaitingPacket) packet, os);
                break;
            case ComputeSalariesPacket:
                handleComputeSalaries((ComputeSalariesPacket) packet, os);
                break;
            case ValidateSalariesPacket:
                handleValidateSalaries((ValidateSalariesPacket) packet, os);
                break;
            default:
                Log.e("Invalid packet received: %s", id);
                break;
        }
    }

    private Tuple<SecretKey, SecretKey> getSession(UUID session) {
        Tuple3<Instant, SecretKey, SecretKey> lastSeen = sessions.get(session);
        if(lastSeen == null) {
            return null;
        }

        Instant now = Instant.now();
        if(lastSeen.t1.plus(SESSION_TIMEOUT).isBefore(now)) {
            sessions.remove(session);
            return null;
        }

        sessions.put(session, new Tuple3<>(now, lastSeen.t2, lastSeen.t3));
        return new Tuple<>(lastSeen.t2, lastSeen.t3);
    }

    private void handleLogin(LoginPacket p, OutputStream os) throws IOException {
        Log.i("Handling login attempt");

        Instant now = Instant.now();
        if(Instant.ofEpochMilli(p.getTime()).plus(TIME_TOLERANCE).isBefore(now)) {
            proto.write(os, LoginResponsePacket.ERROR);
            return;
        }

        Optional<Staff> staff;
        try {
            staff = accounting.table(Staff.class).findOne(DBPredicate.of("login", p.getUsername())).get();
        } catch(InterruptedException e) {
            Log.e("Got interrupted");
            Thread.currentThread().interrupt();
            return;
        } catch(ExecutionException e) {
            Log.e("Something bad happened while fetching", e);
            proto.write(os, LoginResponsePacket.ERROR);
            return;
        }

        if(!staff.isPresent()) {
            proto.write(os, LoginResponsePacket.ERROR);
            return;
        }

        PrivateKey serverKey;
        try {
            KeyStore jks = KeyStore.getInstance("JKS");
            jks.load(Files.newInputStream(Paths.get(
                conf.getStringThrowing("accounting_server.private_key.path"))),
                conf.getStringThrowing("accounting_server.private_key.password").toCharArray());
            serverKey = (PrivateKey) jks.getKey(
                conf.getStringThrowing("accounting_server.private_key.alias"),
                conf.getStringThrowing("accounting_server.private_key.password").toCharArray());
        } catch(KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException e) {
            Log.e("%d :(", e);
            throw new RuntimeException(e);
        }

        if(Digestion.check(p.getDigest(), staff.get().getPassword(), p.getTime(), p.getSalt())) {
            UUID session = UUID.randomUUID();
            SecretKey signKey = new SecretKeySpec(Cipheriscope.decrypt(serverKey, p.getSignKeyCiphered()), "AES");
            SecretKey cryptKey = new SecretKeySpec(Cipheriscope.decrypt(serverKey, p.getCryptKeyCiphered()), "AES");
            sessions.put(session, new Tuple3<>(now, signKey, cryptKey));

            Log.d("%s logged in successfully", p.getUsername());
            proto.write(os, new LoginResponsePacket(session));
        } else {
            proto.write(os, LoginResponsePacket.ERROR);
        }
    }

    private void handleGetNextBill(GetNextBillPacket p, OutputStream os) throws IOException {
        Tuple<SecretKey, SecretKey> sessionKeys = getSession(p.getSession());
        if(sessionKeys == null) {
            proto.write(os, new GetNextBillResponsePacket(null));
            return;
        }

        Optional<Bill> oldestBillOpt;
        try {
            oldestBillOpt = accounting.table(Bill.class).find(DBPredicate.of("validated", '0')).get()
                .sorted(Comparator.comparing(Bill::getBillDate))
                .findFirst();
        } catch(InterruptedException e) {
            Log.e("Interrupted getting next bill", e);
            Thread.currentThread().interrupt();
            return;
        } catch(ExecutionException e) {
            Log.e("Error getting oldest bill", e);
            proto.write(os, new GetNextBillResponsePacket(null));
            return;
        }

        if(!oldestBillOpt.isPresent()) {
            proto.write(os, new GetNextBillResponsePacket(null));
            return;
        }

        Bill oldestBill = oldestBillOpt.get();

        String toSend = String.format("%d:%d:%s:%f:%f",
            oldestBill.getBillId(),
            oldestBill.getCompanyId(),
            oldestBill.getBillDate(),
            oldestBill.getTotalPriceExcludingVat(),
            oldestBill.getTotalPriceIncludingVat());

        proto.write(os, new GetNextBillResponsePacket(Cipheriscope.encrypt(
            sessionKeys.second, toSend.getBytes())));
    }

    private void handleValidateBill(ValidateBillPacket p, OutputStream os) throws IOException {
        Tuple<SecretKey, SecretKey> sessionKeys = getSession(p.getSession());
        if(sessionKeys == null) {
            proto.write(os, new GetNextBillResponsePacket(null));
            return;
        }

    }

    private void handleListBills(ListBillsPacket p, OutputStream os) {

    }

    private void handleSendBills(SendBillsPacket p, OutputStream os) {

    }

    private void handleRecPay(RecPayPacket p, OutputStream os) {

    }

    private void handleListWaiting(ListWaitingPacket p, OutputStream os) {

    }

    private void handleComputeSalaries(ComputeSalariesPacket p, OutputStream os) {

    }

    private void handleValidateSalaries(ValidateSalariesPacket p, OutputStream os) {
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {

    }
}
