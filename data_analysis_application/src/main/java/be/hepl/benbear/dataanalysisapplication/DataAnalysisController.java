package be.hepl.benbear.dataanalysisapplication;

import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.commons.jfx.Inputs;
import be.hepl.benbear.pidep.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DataAnalysisController implements Initializable {

    private final DataAnalysisApplication app;
    private LoginController loginController;
    @FXML private TabPane tabPane;
    @FXML private Tab loginTab;
    @FXML private Tab containerDescriptiveStatisticTab;
    @FXML private ChoiceBox<GetContainerDescriptiveStatisticPacket.Type> containerDescriptiveStatisticTypeBox;
    @FXML private TextField containerDescriptiveStatisticSampleSizeField;
    @FXML private Button containerDescriptiveStatisticButton;
    @FXML private TableView<Tuple<String, String>> containerDescriptiveStatisticTable;
    @FXML private TableColumn<Tuple<String, String>, String> containerDescriptiveStatisticNameColumn;
    @FXML private TableColumn<Tuple<String, String>, String> containerDescriptiveStatisticValueColumn;
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
    @FXML private TextField statInferHomogeneityTestFirstCityField;
    @FXML private TextField statInferHomogeneityTestSecondCityField;
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

        // TODO Disable default buttons on inactive panes, they seem to interfere

        Inputs.integer(containerDescriptiveStatisticSampleSizeField, 2, Integer.MAX_VALUE);
        Inputs.integer(containerPerDestinationGraphTimeField, 0, Integer.MAX_VALUE);
        Inputs.integer(containerPerDestinationPerQuarterGraphYearField, 0, Integer.MAX_VALUE);
        Inputs.integer(statInferConformityTestSampleSizeField, 2, Integer.MAX_VALUE);
        Inputs.integer(statInferHomogeneityTestSampleSizeField, 2, Integer.MAX_VALUE);

        containerDescriptiveStatisticNameColumn.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().first));
        containerDescriptiveStatisticValueColumn.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().second));
        containerDescriptiveStatisticTypeBox.getItems().setAll(
            GetContainerDescriptiveStatisticPacket.Type.IN,
            GetContainerDescriptiveStatisticPacket.Type.OUT
        );
        containerDescriptiveStatisticButton.setOnAction(e -> app.send(new GetContainerDescriptiveStatisticPacket(
            app.getSession(),
            Integer.parseInt(containerDescriptiveStatisticSampleSizeField.getText()),
            containerDescriptiveStatisticTypeBox.getSelectionModel().getSelectedItem()
        )));

        containerPerDestinationGraphTypeBox.getItems().setAll(
            GetContainerPerDestinationGraphPacket.Type.MONTHLY,
            GetContainerPerDestinationGraphPacket.Type.YEARLY
        );
        containerPerDestinationGraphButton.setOnAction(e -> app.send(new GetContainerPerDestinationGraphPacket(
            app.getSession(),
            containerPerDestinationGraphTypeBox.getSelectionModel().getSelectedItem(),
            Integer.parseInt(containerPerDestinationGraphTimeField.getText())
        )));

        containerPerDestinationPerQuarterGraphButton.setOnAction(e -> app.send(new GetContainerPerDestinationPerQuarterGraphPacket(
            app.getSession(),
            Integer.parseInt(containerPerDestinationPerQuarterGraphYearField.getText())
        )));

        statInferConformityTestButton.setOnAction(e -> app.send(new GetStatInferConformityTestPacket(
            app.getSession(),
            Integer.parseInt(statInferConformityTestSampleSizeField.getText())
        )));

        statInferHomogeneityTestButton.setOnAction(e -> app.send(new GetStatInferHomogeneityTestPacket(
            app.getSession(),
            Integer.parseInt(statInferHomogeneityTestSampleSizeField.getText()),
            statInferHomogeneityTestFirstCityField.getText(),
            statInferHomogeneityTestSecondCityField.getText()
        )));

        statInferAnovaTestButton.setOnAction(e -> app.send(new GetStatInferAnovaTestPacket(
            app.getSession(),
            Integer.parseInt(statInferAnovaTestSampleSizeField.getText())
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
                GetContainerDescriptiveStatisticResponsePacket p1 = (GetContainerDescriptiveStatisticResponsePacket) packet;
                containerDescriptiveStatisticTable.getItems().setAll(
                    new Tuple<>("Average", String.valueOf(p1.getAverage())),
                    new Tuple<>("Median", String.valueOf(p1.getMedian())),
                    new Tuple<>("Modes", Arrays.stream(p1.getModes()).mapToObj(String::valueOf).collect(Collectors.joining(", "))),
                    new Tuple<>("Standard deviation", String.valueOf(p1.getStd()))
                );
                break;
            case GetContainerPerDestinationGraphResponse: {
                GetContainerPerDestinationGraphResponsePacket p2 = (GetContainerPerDestinationGraphResponsePacket) packet;
                setImage(
                    containerPerDestinationGraphImage,
                    new Image(new ByteArrayInputStream(p2.getBytes()))
                );
                break;
            }
            case GetContainerPerDestinationPerQuarterGraphResponse:
                GetContainerPerDestinationPerQuarterGraphResponsePacket p3 = (GetContainerPerDestinationPerQuarterGraphResponsePacket) packet;
                setImage(
                    containerPerDestinationPerQuarterGraphImage,
                    new Image(new ByteArrayInputStream(p3.getBytes()))
                );
                break;
            case GetStatInferConformityTestResponse:
                GetStatInferConformityTestResponsePacket p4 = (GetStatInferConformityTestResponsePacket) packet;
                statInferConformityTestResultLabel.setText("The hypothesis that the average time spent in the parc is 10 days is to be "
                    + (p4.isSignificant() ? "rejected" : "accepted") + ". p-Value = " + p4.getpValue());
                break;
            case GetStatInferHomogeneityTestResponse:
                GetStatInferHomogeneityTestResponsePacket p5 = (GetStatInferHomogeneityTestResponsePacket) packet;
                statInferHomogeneityTestResultLabel.setText("The hypothesis that the average time spent in the parc is the same for these two destinations is to be "
                    + (p5.isSignificant() ? "rejected" : "accepted") + ". p-Value = " + p5.getpValue());
                break;
            case GetStatInferAnovaTestResponse:
                GetStatInferAnovaTestResponsePacket p6 = (GetStatInferAnovaTestResponsePacket) packet;
                statInferAnovaTestResultLabel.setText("The hypothesis that the average time spent in the parc is the same for all the destinations is to be "
                    + (p6.isSignificant() ? "rejected" : "accepted") + ". p-Value = " + p6.getpValue());
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
