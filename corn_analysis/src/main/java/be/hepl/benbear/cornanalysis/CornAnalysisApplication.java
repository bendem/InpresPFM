package be.hepl.benbear.cornanalysis;

import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.cornanalysis.parser.CornStat;
import be.hepl.benbear.cornanalysis.parser.Parser;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.util.List;

public class CornAnalysisApplication extends BaseApplication {

    private CornStat data;

    public static void main(String... args) {
        launch(args);
    }


    public CornAnalysisApplication() {
        super(getResource("style.css"));
    }

    @Override
    public void start(Stage stage) throws Exception {
        Log.d("starting");
        List<String> raw = getParameters().getRaw();

        if(raw.isEmpty()){
            throw new Exception();
        }

        data = new Parser(Paths.get(raw.get(0))).parse();
        open("Display.fxml", "InpresFPM - Data Analysis of CORN", false, true);
    }

    @Override
    public void stop() throws Exception {
        Log.d("Stopping");
    }

    public CornStat getData() {
        return data;
    }
}
