package be.hepl.benbear.containerserveradmin;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.containerserveradmin.proto.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContainerServerAdmin extends BaseApplication {

    public static void main(String... args) {
        launch(args);
    }

    private final Config config;
    private final ProtocolHandler protocolHandler;
    private final ExecutorService threadPool;
    private Socket socket;

    public ContainerServerAdmin() {
        config = new Config();
        protocolHandler = new ProtocolHandler();
        threadPool = Executors.newSingleThreadExecutor();

        protocolHandler.registerPacket(LoginPacket.ID, LoginPacket.class);
        protocolHandler.registerPacket(LoginResponsePacket.ID, LoginResponsePacket.class);
        protocolHandler.registerPacket(ListPacket.ID, ListPacket.class);
        protocolHandler.registerPacket(ListResponsePacket.ID, ListResponsePacket.class);
        protocolHandler.registerPacket(PausePacket.ID, PausePacket.class);
        protocolHandler.registerPacket(PauseReponsePacket.ID, PauseReponsePacket.class);
        protocolHandler.registerPacket(StopPacket.ID, StopPacket.class);
        protocolHandler.registerPacket(StopResponsePacket.ID, StopResponsePacket.class);
    }

    public Config getConfig() {
        return config;
    }

    public <T extends Packet> CompletableFuture<T> send(Packet packet, Class<T> response) {
        CompletableFuture<T> future = new CompletableFuture<>();

        threadPool.submit(() -> {
            try {
                protocolHandler.write(socket.getOutputStream(), packet);
                future.complete(protocolHandler.readSpecific(socket.getInputStream(), response));
            } catch(IOException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Log.d("starting");
        config.load(getParameters().getNamed().get("config"));
        socket = new Socket(
            config.getString("containerserver.host").orElse("localhost"),
            config.getInt("containerserver.admin.port").orElse(31069)
        );
        AdminController ctrl = open("admin.fxml", "InpresFPM - Container Server Admin", false, true);
        this.<LoginController>open("login.fxml", "InpresFPM - Login", true)
            .setAdminController(ctrl);
    }

    @Override
    public void stop() throws Exception {
        Log.d("stopping");
        threadPool.shutdown();
        socket.close();
    }

}
