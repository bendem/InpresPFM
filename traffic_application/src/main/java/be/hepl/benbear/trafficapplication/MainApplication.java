package be.hepl.benbear.trafficapplication;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.protocol.tramap.*;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainApplication extends BaseApplication {

    public static void main(String[] args) {
        launch(args);
    }

    private final Config conf = new Config();
    private final ProtocolHandler prot = new ProtocolHandler();
    private Socket socket;
    private InputStream ois = null;
    private OutputStream oos = null;

    private boolean connected = false;

    public MainApplication() {
        super(getResource("style.css"));
    }

    @Override
    public void start(Stage stage) throws IOException {
        open("MainApplication.fxml", "Traffic Application", true);
    }

    @Override
    public void init() throws Exception {
        socket = new Socket(InetAddress.getByName(conf.getString("trafficApplication.ip").orElse("localhost")), conf.getInt("trafficApplication.port").orElse(31065));
        oos = new DataOutputStream(socket.getOutputStream());
        ois = new DataInputStream(socket.getInputStream());

        prot.registerPacket(LoginPacket.ID, LoginPacket.class);
        prot.registerPacket(LoginResponsePacket.ID, LoginResponsePacket.class);
        prot.registerPacket(InputLorryPacket.ID, InputLorryPacket.class);
        prot.registerPacket(InputLorryResponsePacket.ID, InputLorryResponsePacket.class);
        prot.registerPacket(InputLorryWithoutReservationPacket.ID, InputLorryWithoutReservationPacket.class);
        prot.registerPacket(InputLorryWithoutReservationResponsePacket.ID, InputLorryWithoutReservationResponsePacket.class);
        prot.registerPacket(ListOperationsPacket.ID, ListOperationsPacket.class);
        prot.registerPacket(ListOperationsResponsePacket.ID, ListOperationsResponsePacket.class);
        prot.registerPacket(LogoutPacket.ID, LogoutPacket.class);
        prot.registerPacket(LogoutResponsePacket.ID, LogoutResponsePacket.class);
        new ContainerPosition("d",0,0);
        new be.hepl.benbear.protocol.tramap.Movement(0,"o","o","o",0,0);
    }

    public Packet read() throws IOException {
        return prot.read(ois);
    }

    public void write(Packet p) throws IOException {
        prot.write(oos, p);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

}
