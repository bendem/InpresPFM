package be.hepl.benbear.chatserver;

import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.pfmcop.LoginPacket;
import be.hepl.benbear.pfmcop.LoginResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ChatServer extends Server<DataInputStream, DataOutputStream> {

    private final Config config;
    private final ProtocolHandler protocolHandler;
    private final SQLDatabase database;

    public ChatServer(Config config) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(config.getString("chatserver.host.tcp").orElse("localhost"))).get(),
            config.getInt("chatserver.port.tcp").orElse(31063),
            Thread::new,
            Executors.newSingleThreadExecutor(),
            DataInputStream::new,
            DataOutputStream::new
        );
        this.config = config;

        Database.Driver.ORACLE.load();

        database = new SQLDatabase();
        database.registerClass(Staff.class);
        database.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.accounting.user").get(),
            config.getString("jdbc.accounting.password").get());

        protocolHandler = new ProtocolHandler();
        protocolHandler.registerPacket(LoginPacket.ID, LoginPacket.class);
        protocolHandler.registerPacket(LoginResponsePacket.ID, LoginResponsePacket.class);
    }

    @Override
    protected void read(DataInputStream is, DataOutputStream os) throws IOException {
        LoginPacket packet = protocolHandler.readSpecific(is, LoginPacket.class);

        if(check(packet.getUsername(), packet.getDigest())) {
            protocolHandler.write(os, new LoginResponsePacket(
                config.getString("chatserver.host.udp").orElse("localhost"),
                config.getInt("chatserver.port.udp").orElse(31064)
            ));
        }
    }

    private boolean check(String username, byte digest) {
        Optional<Staff> staff;
        try {
            staff = database.table(Staff.class).findOne(DBPredicate.of("login", username)).get();
        } catch(InterruptedException | ExecutionException e) {
            Log.e("Failed to retrieve user", e);
            return false;
        }

        return staff.isPresent() && LoginPacket.digest(staff.get().getPassword()) == digest;
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {

    }
}
