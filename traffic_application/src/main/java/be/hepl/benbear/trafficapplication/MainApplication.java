package be.hepl.benbear.trafficapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class MainApplication extends Application {

    private Stage mainStage;

    private final Map<Object, WeakReference<Stage>> stages;
    private boolean connected = false;

    public MainApplication() {
        stages = new WeakHashMap<>();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getStage(Object controller) {
        return stages.get(controller).get();
    }

    @Override
    public void start(Stage stage) throws IOException {
        open("MainApplication.fxml", "Traffic Application", true);
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
                return clazz.getConstructor(MainApplication.class).newInstance(this);
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
            if(modal) {
                stage.initModality(Modality.WINDOW_MODAL);
            }
            stage.initOwner(mainStage);
        }
        app.getStylesheets().add(getResource("style.css").toExternalForm());
        stage.setTitle(title);
        stage.setScene(new Scene(app));
        stage.show();

        T controller = loader.getController();
        stages.put(controller, new WeakReference<>(stage));
        return controller;
    }

    @Override
    public void init() throws Exception {

    }

    public Stage getMainStage() {
        return mainStage;
    }

    private URL getResource(String name) {
        return Objects.requireNonNull(getClass().getClassLoader().getResource(name), "Resource not found: " + name);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
