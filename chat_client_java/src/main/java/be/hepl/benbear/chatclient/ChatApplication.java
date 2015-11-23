package be.hepl.benbear.chatclient;

import be.hepl.benbear.commons.checking.Sanity;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.pfmcop.LoginPacket;
import be.hepl.benbear.pfmcop.LoginResponsePacket;
import be.hepl.benbear.pfmcop.UDPPacket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatApplication extends Application {

    public static void main(String... args) {
        launch(args);
    }

    private final ExecutorService threadPool;
    private final Config config;
    private final Map<Object, WeakReference<Stage>> stages;
    private final ObservableList<Message> messages;
    private Stage mainStage;
    private MulticastSocket socket;
    private SocketAddress address;

    public ChatApplication() {
        threadPool = Executors.newFixedThreadPool(2);
        config = new Config();
        stages = new WeakHashMap<>();
        messages = FXCollections.observableArrayList();
    }

    public Config getConfig() {
        return config;
    }

    public Stage getStage(Object controller) {
        return stages.get(controller).get();
    }

    public void send(UDPPacket packet) {
        Sanity.notNull(socket, "Socket not yet initialized");

        try {
            byte[] serialized = BinarySerializer.getInstance().serialize(packet);
            ByteBuffer buffer = ByteBuffer.allocate(serialized.length + 2);
            buffer.put((byte) (serialized.length >> 8));
            buffer.put((byte) (serialized.length & 0xff));
            buffer.put(serialized);

            Log.d("Sending %d bytes: %s", serialized.length, Arrays.toString(serialized));

            DatagramPacket datagramPacket = new DatagramPacket(buffer.array(), 0, serialized.length + 2, address);
            socket.send(datagramPacket);
        } catch(IOException e) {
            Log.e("Failed to send message", e);
        }
    }

    public ObservableList<Message> messagesProperty() {
        return FXCollections.unmodifiableObservableList(messages);
    }

    public CompletableFuture<Tuple<String, Integer>> checkLogin(String username, String password) {
        CompletableFuture<Tuple<String, Integer>> future = new CompletableFuture<>();
        threadPool.submit(() -> {
            try (Socket socket = new Socket(
                config.getString("chatserver.host.tcp").orElse("localhost"),
                config.getInt("chatserver.port.tcp").orElse(31063)
            )) {
                ProtocolHandler protocolHandler = new ProtocolHandler();
                protocolHandler.registerPacket(LoginPacket.ID, LoginPacket.class);
                protocolHandler.registerPacket(LoginResponsePacket.ID, LoginResponsePacket.class);

                Log.d("Contacting server %s", socket.getRemoteSocketAddress());

                protocolHandler.write(socket.getOutputStream(), new LoginPacket(username, password));
                LoginResponsePacket response = protocolHandler.readSpecific(socket.getInputStream(), LoginResponsePacket.class);
                future.complete(new Tuple<>(response.getHost(), response.getPort()));
            } catch(IOException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public void startChat(String host, int port) {
        try {
            Log.d("Creating multicast socket on %s:%d", host, port);
            address = new InetSocketAddress(InetAddress.getByName(host), port);
            socket = new MulticastSocket(port);
            Optional<String> interfaceOption = config.getString("chatclient.network_interface");
            NetworkInterface networkInterface;
            if(interfaceOption.isPresent()) {
                networkInterface = NetworkInterface.getByName(interfaceOption.get());
            } else {
                networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            }
            Log.d("Joining multicast group on %s", networkInterface);
            socket.joinGroup(address, networkInterface);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        threadPool.submit(new UDPReceiver(
            socket, address,
            (Message m) -> Platform.runLater(() -> messages.add(m)),
            threadPool::isShutdown
        ));
    }

    @Override
    public void start(Stage stage) throws IOException {
        Log.d("starting");
        config.load(getParameters().getNamed().get("config"));
        ChatController ctrl = open("chat.fxml", "InpresFPM - Chat", false, true);
        this.<LoginController>open("login.fxml", "InpresFPM - Login", true)
            .setChatController(ctrl);
    }

    @Override
    public void stop() throws Exception {
        Log.d("stopping");
        if(socket != null) {
            socket.close();
        }
        threadPool.shutdown();
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
                return clazz.getConstructor(ChatApplication.class).newInstance(this);
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
        app.getStylesheets().add(getResource("style.css").toExternalForm());
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
