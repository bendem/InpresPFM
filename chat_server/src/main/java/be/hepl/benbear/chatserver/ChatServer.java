package be.hepl.benbear.chatserver;

import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.pfmcop.LoginPacket;
import be.hepl.benbear.pfmcop.LoginResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

public class ChatServer extends Server<DataInputStream, DataOutputStream> {

    private final ProtocolHandler protocolHandler;
    private final SQLDatabase database;

    public ChatServer(int port) {
        super(
            port, Thread::new,
            Executors.newSingleThreadExecutor(),
            DataInputStream::new,
            DataOutputStream::new
        );

        Database.Driver.ORACLE.load();

        database = new SQLDatabase();
        database.registerClass(Staff.class);
        database.connect("jdbc:oracle:thin:@178.32.41.4:8080:xe", "accounting", "bleh");

        protocolHandler = new ProtocolHandler();
        protocolHandler.registerPacket((byte) 1, LoginPacket.class);
        protocolHandler.registerPacket((byte) 2, LoginResponsePacket.class);
    }

    @Override
    protected void read(DataInputStream is, DataOutputStream os) throws IOException {
        LoginPacket packet = protocolHandler.readSpecific(is, LoginPacket.class);

        if(check(packet.getUsername(), packet.getDigest())) {
            os.writeShort(31064);
        }
    }

    private boolean check(String username, byte digest) {
        // TODO Implement stuff

        return true;
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {

    }
}
