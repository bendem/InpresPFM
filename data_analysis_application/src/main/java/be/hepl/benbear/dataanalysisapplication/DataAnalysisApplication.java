package be.hepl.benbear.dataanalysisapplication;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.pidep.ErrorPacket;
import be.hepl.benbear.pidep.LoginReponsePacket;
import be.hepl.benbear.pidep.Packet;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataAnalysisApplication extends BaseApplication {

    private UUID session;
    private Thread sender;

    public static void main(String... args) {
        launch(args);
    }

    private final Config config;
    private final ExecutorService threadPool;
    private Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private DataAnalysisController dataAnalysisController;

    public DataAnalysisApplication() {
        super(getResource("style.css"));
        config = new Config();
        threadPool = Executors.newSingleThreadExecutor();
    }

    public void send(Packet packet) {
        threadPool.submit(() -> {
            try {
                os.writeObject(packet);
            } catch(IOException e) {
                Log.e("Failed to send %s packet", e, packet);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to send packet: " + e.getMessage());
                    alert.initOwner(getMainStage());
                    alert.show();
                });
            }
        });
    }

    public void receive() {
        while(!Thread.interrupted() && !socket.isClosed()) {
            Packet packet;
            try {
                packet = (Packet) is.readObject();
            } catch(InterruptedIOException e) {
                return;
            } catch(IOException | ClassNotFoundException | ClassCastException e) {
                Log.e("Error receiving packet", e);
                if(e instanceof EOFException) {
                    // FIXME This should disconnect instead
                    close();
                    return;
                }
                continue;
            }

            Platform.runLater(() -> {
                if(packet.getId() == Packet.Id.LoginResponse) {
                    session = ((LoginReponsePacket) packet).getSession();
                    dataAnalysisController.connected();
                } else if(packet.getId() == Packet.Id.Error) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "An error happened: " + ((ErrorPacket) packet).getMessage());
                    alert.initOwner(getMainStage());
                    alert.showAndWait();
                    dataAnalysisController.getLoginController().resetError();
                } else {
                    dataAnalysisController.handle(packet);
                }
            });
        }
    }

    public UUID getSession() {
        return session;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Log.d("starting");
        config.load(getParameters().getNamed().get("config"));

        socket = new Socket(
            InetAddress.getByName(config.getString("dataanalysisserver.host").orElse("localhost")),
            config.getInt("dataanalysisserver.port").getAsInt()
        );

        os = new ObjectOutputStream(socket.getOutputStream());
        is = new ObjectInputStream(socket.getInputStream());

        sender = new Thread(this::receive);
        sender.start();

        dataAnalysisController = open("DataAnalysisApplication.fxml", "InpresFPM - Data Analysis", false, true);
        //open("login.fxml", "InpresFPM - Login", true);
    }

    @Override
    public void stop() throws Exception {
        Log.d("Stopping");

        sender.interrupt();

        if(is != null) {
            is.close();
        }
        if(os != null) {
            os.close();
        }
        if(socket != null && !socket.isClosed()) {
            socket.close();
        }

        threadPool.shutdown();
    }
}
