package be.hepl.benbear.accountingclient;

import be.hepl.benbear.bisamap.*;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

import javax.crypto.SecretKey;

public class AccountingApplication extends BaseApplication {

    public static void main(String... args) {
        launch(args);
    }

    private final ProtocolHandler proto;
    private final Config conf;
    private Socket socket;
    private OutputStream os;
    private InputStream is;

    public AccountingApplication() {
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
        conf = new Config();
    }

    @Override
    public void init() throws Exception {
        conf.load(getParameters().getNamed().get("config"));
        socket = new Socket(InetAddress.getByName(conf.getStringThrowing("accounting_server.host")),
            conf.getIntThrowing("accounting_server.port"));
        os = socket.getOutputStream();
        is = socket.getInputStream();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AccountingController ctrl = open("Accounting.fxml", "Accounting - InpresFPM", false, true);
        this.<LoginController>open("Login.fxml", "Login", true).setMainController(ctrl);
    }

    public void write(Packet packet) {
        try {
            proto.write(os, packet);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Packet> T readSpecific(Class<T> clazz) {
        try {
            return proto.readSpecific(is, clazz);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(UUID session, SecretKey signKey, SecretKey cryptKey) {
        Log.d("Logged in \\o/");

    }

    public Config getConfig() {
        return conf;
    }
}
