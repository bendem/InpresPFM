package be.hepl.benbear.dataanalysisapplication;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import javafx.stage.Stage;

public class DataAnalysisApplication extends BaseApplication {

    public static void main(String... args) {
        launch(args);
    }

    private final Config config;

    public DataAnalysisApplication() {
        super(getResource("style.css"));
        config = new Config();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Log.d("starting");
        config.load(getParameters().getNamed().get("config"));
        open("DataAnalysisApplication.fxml", "InpresFPM - Data Analysis", false, true);
    }
}
