package be.hepl.benbear.dataanalysisserver;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

public class DataAnalysisServer extends Server<ObjectInputStream, ObjectOutputStream> {

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
    }

    @Override
    protected void read(ObjectInputStream is, ObjectOutputStream os) throws IOException {
        // TODO
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        if(e != null) {
            Log.e("%s errored", e, channel);
        }
    }
}
