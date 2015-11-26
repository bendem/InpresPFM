package be.hepl.benbear.dataanalysisapplication;

import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.pidep.GetContainerDescriptiveStatisticPacket;
import be.hepl.benbear.pidep.GetContainerPerDestinationGraphPacket;
import be.hepl.benbear.pidep.GetContainerPerDestinationGraphResponsePacket;
import be.hepl.benbear.pidep.Packet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DataAnalysisController implements Initializable {

    private final DataAnalysisApplication app;
    private LoginController loginController;
    @FXML private TabPane tabPane;
    @FXML private Tab loginTab;
    @FXML private Tab containerDescriptiveStatisticTab;
    @FXML private ChoiceBox<GetContainerDescriptiveStatisticPacket.Type> containerDescriptiveStatisticTypeBox;
    @FXML private TextField containerDescriptiveStatisticSampleSizeField;
    @FXML private Button containerDescriptiveStatisticButton;
    @FXML private TableView<Tuple<String, ?>> containerDescriptiveStatisticTable;
    @FXML private TableColumn<Tuple<String, ?>, String> containerDescriptiveStatisticNameColumn;
    @FXML private TableColumn<Tuple<String, ?>, String> containerDescriptiveStatisticValueColumn;
    @FXML private Tab containerPerDestinationGraphTab;
    @FXML private ChoiceBox<GetContainerPerDestinationGraphPacket.Type> containerPerDestinationGraphTypeBox;
    @FXML private TextField containerPerDestinationGraphTimeField;
    @FXML private Button containerPerDestinationGraphButton;
    @FXML private ImageView containerPerDestinationGraphImage;
    @FXML private Tab containerPerDestinationPerQuarterGraphTab;
    @FXML private TextField containerPerDestinationPerQuarterGraphYearField;
    @FXML private Button containerPerDestinationPerQuarterGraphButton;
    @FXML private ImageView containerPerDestinationPerQuarterGraphImage;
    @FXML private Tab statInferConformityTestTab;
    @FXML private TextField statInferConformityTestSampleSizeField;
    @FXML private Button statInferConformityTestButton;
    @FXML private Label statInferConformityTestResultLabel;
    @FXML private Tab statInferHomogeneityTestTab;
    @FXML private TextField statInferHomogeneityTestSampleSizeField;
    @FXML private Button statInferHomogeneityTestButton;
    @FXML private Label statInferHomogeneityTestResultLabel;
    @FXML private Tab statInferAnovaTestTab;
    @FXML private TextField statInferAnovaTestSampleSizeField;
    @FXML private Button statInferAnovaTestButton;
    @FXML private Label statInferAnovaTestResultLabel;

    public DataAnalysisController(DataAnalysisApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Tuple<Parent, LoginController> loaded = app.loadNode("login.fxml");
            loginTab.setContent(loaded.first);
            loginController = loaded.second;
        } catch(IOException e) {
            throw new RuntimeException("Could not load login node", e);
        }

        // TODO Only allow ints in fields
        // TODO Disable default buttons on inactive panes, they seem to interfere
        // TODO Bind all the things
        containerPerDestinationGraphTypeBox.getItems().setAll(
            GetContainerPerDestinationGraphPacket.Type.MONTHLY,
            GetContainerPerDestinationGraphPacket.Type.YEARLY
        );
        containerPerDestinationGraphButton.setOnAction(e -> app.send(new GetContainerPerDestinationGraphPacket(
            app.getSession(),
            containerPerDestinationGraphTypeBox.getSelectionModel().getSelectedItem(),
            Integer.parseInt(containerPerDestinationGraphTimeField.getText())
        )));
    }

    private void setImage(ImageView view, Image image) {
        Bounds bounds = view.getParent().getBoundsInLocal();
        view.setFitWidth(bounds.getWidth());
        view.setFitHeight(bounds.getHeight());
        view.setImage(image);
    }

    public void handle(Packet packet) {
        switch(packet.getId()) {
            case GetContainerDescriptiveStatisticResponse:
                break;
            case GetContainerPerDestinationGraphResponse: {
                GetContainerPerDestinationGraphResponsePacket p = (GetContainerPerDestinationGraphResponsePacket) packet;
                setImage(
                    containerPerDestinationGraphImage,
                    new Image(new ByteArrayInputStream(p.getBytes()))
                );
                break;
            }
            case GetContainerPerDestinationPerQuarterGraphResponse:
                break;
            case GetStatInferConformityTestResponse:
                break;
            case GetStatInferHomogeneityTestResponse:
                break;
            case GetStatInferAnovaTestResponse:
                break;
        }
    }

    public void connected() {
        containerDescriptiveStatisticTab.setDisable(false);
        containerPerDestinationGraphTab.setDisable(false);
        containerPerDestinationPerQuarterGraphTab.setDisable(false);
        statInferConformityTestTab.setDisable(false);
        statInferHomogeneityTestTab.setDisable(false);
        statInferAnovaTestTab.setDisable(false);

        tabPane.getSelectionModel().clearAndSelect(1);
        loginTab.setDisable(true);
    }

    public LoginController getLoginController() {
        return loginController;
    }

}
