package be.hepl.benbear.accountingserver;

import be.hepl.benbear.chamap.LoginPacket;
import be.hepl.benbear.chamap.LoginResponsePacket;
import be.hepl.benbear.chamap.MakeBillPacket;
import be.hepl.benbear.chamap.MakeBillResponsePacket;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolException;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

public class AccountingBillServer extends Server<DataInputStream, DataOutputStream> {

    private final ProtocolHandler proto;

    public AccountingBillServer(Config config) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(config.getStringThrowing("accounting.host"))).get(),
            config.getIntThrowing("accountingserver.port"),
            Thread::new,
            Executors.newSingleThreadExecutor(),
            DataInputStream::new,
            DataOutputStream::new
        );
        proto = new ProtocolHandler();
        proto
            .registerPacket((byte) 1, LoginPacket.class)
            .registerPacket((byte) 2, LoginResponsePacket.class)
            .registerPacket((byte) 3, MakeBillPacket.class)
            .registerPacket((byte) 4, MakeBillResponsePacket.class)
            ;

        Database db = new SQLDatabase()
            .registerClass(

            );
    }

    @Override
    protected void read(DataInputStream is, DataOutputStream os) throws IOException {
        Packet packet = proto.read(is);
        switch(packet.getId()) {
            case 1:
                handleLogin((LoginPacket) packet);
                break;
            case 2:
                handleMakeBill((MakeBillPacket) packet);
            default:
                throw new ProtocolException(String.format("Invalid id received: %s", packet.getId()));
        }
    }

    private void handleLogin(LoginPacket packet) {
        Log.i("Handling login attempt");

        // TODO
    }

    private void handleMakeBill(MakeBillPacket packet) {
        Log.i("Making bills");
        // TODO
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        // TODO
    }
}
