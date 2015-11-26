package be.hepl.benbear.commons.jfx;

import be.hepl.benbear.commons.generics.Tuple;
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
import java.util.*;

public abstract class BaseApplication extends Application {

    private final Map<Object, WeakReference<Stage>> stages;
    private final Set<URL> stylesheets;
    private Stage mainStage;

    protected BaseApplication() {
        this(Collections.emptySet());
    }

    protected BaseApplication(URL... stylesheets) {
        this(Arrays.asList(stylesheets));
    }

    protected BaseApplication(Collection<URL> stylesheets) {
        this.stylesheets = new HashSet<>(stylesheets);
        stages = new WeakHashMap<>();
    }

    public Stage getStage(Object controller) {
        return stages.get(controller).get();
    }

    public Stage getMainStage() {
        return mainStage;
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

    protected <T> T open(String fxml, String title, boolean modal, boolean main) throws IOException {
        if(main && modal) {
            throw new IllegalArgumentException("The main window can't be modal");
        }

        FXMLLoader loader = load(getResource(fxml));
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

        for(URL stylesheet : stylesheets) {
            app.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle(title);
        stage.setScene(new Scene(app));
        stage.show();

        T controller = loader.getController();
        stages.put(controller, new WeakReference<>(stage));
        return controller;
    }

    private FXMLLoader load(URL fxml) {
        FXMLLoader loader = new FXMLLoader(fxml);

        loader.setControllerFactory(clazz -> {
            try {
                return clazz.getConstructor(getClass()).newInstance(this);
            } catch(InstantiationException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("Could not instantiate controller for " + clazz, e);
            }
        });

        return loader;
    }

    public <T> Tuple<Parent, T> loadNode(String fxml) throws IOException {
        FXMLLoader loader = load(getResource(fxml));
        return new Tuple<>(loader.load(), loader.getController());
    }

    protected static URL getResource(String name) {
        return getResource(BaseApplication.class, name);
    }

    protected static URL getResource(Class<?> clazz, String name) {
        return Objects.requireNonNull(clazz.getClassLoader().getResource(name), "Resource not found: " + name);
    }

}
