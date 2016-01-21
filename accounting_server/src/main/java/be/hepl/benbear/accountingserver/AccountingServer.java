package be.hepl.benbear.accountingserver;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

public class AccountingServer extends Server<DataInputStream, DataOutputStream> {

    private final Database accounting;
    private final Database traffic;

    public AccountingServer(Config conf, Database accounting, Database traffic) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(conf.getStringThrowing("accounting.host"))).get(),
            conf.getIntThrowing("accounting_server.port"),
            Thread::new,
            Executors.newSingleThreadExecutor(),
            DataInputStream::new,
            DataOutputStream::new
        );
        this.accounting = accounting;
        this.traffic = traffic;
    }

    @Override
    protected void read(DataInputStream is, DataOutputStream os) throws IOException {

    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {

    }
}
