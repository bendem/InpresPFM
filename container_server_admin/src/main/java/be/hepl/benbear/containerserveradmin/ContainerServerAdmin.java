package be.hepl.benbear.containerserveradmin;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.containerserveradmin.proto.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContainerServerAdmin extends Application {

    public static void main(String... args) {
        launch(args);
    }

    private final Config config;
    private final ProtocolHandler protocolHandler;
    private final ExecutorService threadPool;
    private Socket socket;
    private Stage mainStage;
    private Map<Object, WeakReference<Stage>> stages;

    public ContainerServerAdmin() {
        config = new Config();
        protocolHandler = new ProtocolHandler();
        threadPool = Executors.newSingleThreadExecutor();
        stages = new WeakHashMap<>();

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
        CompletableFuture<T> future = CompletableFuture.completedFuture(null);

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

    public Stage getStage(Object controller) {
        return stages.get(controller).get();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Log.d("starting");
        config.load(getParameters().getNamed().get("config"));
        socket = new Socket(
            config.getString("container_server.host").orElse("localhost"),
            config.getInt("container_server.port").orElse(31060)
        );
        AdminController ctrl = open("admin.fxml", "InpresFPM - Container Server Admin", false, true);
        this.<LoginController>open("login.fxml", "InpresFPM - Login", true)
            .setAdminController(ctrl);
    }

    @Override
    public void stop() throws Exception {
        Log.d("stopping");
    }

    public void close() {
        stages.values().stream()
            .map(WeakReference::get)
            .filter(s -> s != null)
            .forEach(Stage::close);
        mainStage.close();
    }

    public <T> T open(String fxml, String title, boolean modal) throws IOException {
        return open(fxml, title, modal, false);
    }

    private <T> T open(String fxml, String title, boolean modal, boolean main) throws IOException {
        if(main && modal) {
            throw new IllegalArgumentException("The main window can't be modal");
        }

        FXMLLoader loader = new FXMLLoader(getResource(fxml));

        loader.setControllerFactory(clazz -> {
            try {
                return clazz.getConstructor(ContainerServerAdmin.class).newInstance(this);
            } catch(InstantiationException | IllegalAccessException
                | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("Could not instantiate controller for " + clazz, e);
            }
        });

        Parent app = loader.load();
        Stage stage = new Stage();
        if(main) {
            this.mainStage = stage;
        } else {
            stage.initOwner(mainStage);
            if(modal) {
                stage.initModality(Modality.WINDOW_MODAL);
            }
        }
        //app.getStylesheets().add(getResource("style.css").toExternalForm());
        stage.setTitle(title);
        stage.setScene(new Scene(app));
        stage.show();

        T controller = loader.getController();
        stages.put(controller, new WeakReference<>(stage));
        return controller;
    }

    private URL getResource(String name) {
        return Objects.requireNonNull(getClass().getClassLoader().getResource(name), "Resource not found: " + name);
    }

}
