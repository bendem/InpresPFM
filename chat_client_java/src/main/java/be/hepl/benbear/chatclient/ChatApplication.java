package be.hepl.benbear.chatclient;

import be.hepl.benbear.commons.checking.Sanity;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.pfmcop.LoginPacket;
import be.hepl.benbear.pfmcop.LoginResponsePacket;
import be.hepl.benbear.pfmcop.UDPPacket;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatApplication extends BaseApplication {

    public static void main(String... args) {
        launch(args);
    }

    private final ExecutorService threadPool;
    private final Config config;
    private final ObservableList<Message> messages;
    private MulticastSocket socket;
    private SocketAddress address;

    public ChatApplication() {
        super(getResource("style.css"));
        threadPool = Executors.newFixedThreadPool(2);
        config = new Config();
        messages = FXCollections.observableArrayList();
    }

    public Config getConfig() {
        return config;
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

}
