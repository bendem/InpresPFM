package be.hepl.benbear.dataanalysisapplication;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DataAnalysisApplication extends BaseApplication {

    public static void main(String... args) {
        launch(args);
    }

    private final Config config;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    public DataAnalysisApplication() {
        super(getResource("style.css"));
        config = new Config();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Log.d("starting");
        config.load(getParameters().getNamed().get("config"));

        Socket socket = new Socket(
            InetAddress.getByName(config.getString("dataanalysisserver.host").orElse("localhost")),
            config.getInt("dataanalysisserver.port").getAsInt()
        );

        is = new ObjectInputStream(socket.getInputStream());
        os = new ObjectOutputStream(socket.getOutputStream());

        open("DataAnalysisApplication.fxml", "InpresFPM - Data Analysis", false, true);
        open("login.fxml", "InpresFPM - Login", true);
    }

    @Override
    public void stop() throws Exception {
        Log.d("Stopping");

        if(is != null) {
            is.close();
        }
        if(os != null) {
            os.close();
        }
    }
}
