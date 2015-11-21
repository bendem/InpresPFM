package be.hepl.benbear.chatclient;

import be.hepl.benbear.pfmcop.UDPPacket;
import javafx.application.Application;
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
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class ChatApplication extends Application {

    public static void main(String... args) {
        launch(args);
    }

    private final Map<Object, WeakReference<Stage>> stages;
    private final ObservableList<Message> messages;
    private Stage mainStage;

    public ChatApplication() {
        stages = new WeakHashMap<>();
        messages = FXCollections.observableArrayList();
    }

    public Stage getStage(Object controller) {
        return stages.get(controller).get();
    }

    public void send(UDPPacket packet) {
        // TODO Send the packet
        addMessage(packet);
    }

    private void addMessage(UDPPacket packet) {
        messages.add(new Message(packet.type, packet.from, packet.content, packet.tag));
    }

    public ObservableList<Message> messagesProperty() {
        return FXCollections.unmodifiableObservableList(messages);
    }

    @Override
    public void start(Stage stage) throws IOException {
        ChatController ctrl = open("chat.fxml", "InpresFPM - Chat", false, true);
        this.<LoginController>open("login.fxml", "InpresFPM - Login", true)
            .setChatController(ctrl);
    }

    @Override
    public void stop() throws Exception {
        // NOP
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
