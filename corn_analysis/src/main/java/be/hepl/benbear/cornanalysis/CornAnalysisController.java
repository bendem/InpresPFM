package be.hepl.benbear.cornanalysis;

import be.hepl.benbear.cornanalysis.parser.Orientation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.Statistics;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;

public class CornAnalysisController implements Initializable{
    private final CornAnalysisApplication app;

    @FXML private Label answer1Label;
    @FXML private Label answer2Label;
    @FXML private Label answer3Label;
    @FXML private Label answer4Label;
    @FXML private Label answer5Label;
    @FXML private Label answer6Label;
    @FXML private Label answer7Label;

    @FXML private ImageView graph5Image;
    @FXML private ImageView graph6Image;
    @FXML private ImageView graph7Image;

    public CornAnalysisController(CornAnalysisApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // QUESTION 1
        answer1Label.setText(TestUtils.tTest(265,
            app.getData().stream()
                .filter(corn -> corn.plot == Orientation.East && corn.height != null)
                .mapToDouble(corn -> corn.height).toArray(), 0.025) ? "Non" : "Oui"
        );

        // QUESTION 2
        answer2Label.setText(TestUtils.tTest(
            app.getData().stream()
                .filter(corn -> corn.plot == Orientation.North && corn.height != null)
                .mapToDouble(corn -> corn.height).toArray(),
            app.getData().stream()
                .filter(corn -> corn.plot == Orientation.South && corn.height != null)
                .mapToDouble(corn -> corn.height).toArray(),
            0.025) ? "Non" : "Oui"
        );


        // QUESTION 3
        Map<String, List<Double>> values = new HashMap<>();
        app.getData().stream()
            .filter(corn -> corn.height != null)
            .forEach(crop -> values
                .computeIfAbsent(crop.plot.getName(), k -> new ArrayList<>())
                .add((double) crop.height));

        List<double[]> dataLists = new ArrayList<>();
        values.forEach((s, doubles) -> dataLists.add(doubles.stream().mapToDouble(d -> d).toArray()));
        answer3Label.setText(TestUtils.oneWayAnovaTest(dataLists, 0.025) ? "Non" : "Oui");


        // QUESTION 4
        Map<String, List<Double>> values2 = new HashMap<>();
        app.getData().stream()
            .filter(corn -> corn.height != null)
            .forEach(crop -> values2
                .computeIfAbsent(crop.plot.getName()+crop.rooting.getName(), k -> new ArrayList<>())
                .add((double) crop.height));

        List<double[]> dataLists2 = new ArrayList<>();
        values2.forEach((s, doubles) -> dataLists2.add(doubles.stream().mapToDouble(d -> d).toArray()));
        answer4Label.setText(TestUtils.oneWayAnovaTest(dataLists2, 0.025) ? "Non" : "Oui");


        // QUESTION 5
        {
            DefaultXYDataset dataSet = new DefaultXYDataset();
            XYSeries serie1 = new XYSeries("Masse/Hauteur");
            Number x[] = new Number[app.getData().getId().size()];
            Number y[] = new Number[app.getData().getId().size()];

            app.getData().stream()
                .filter(corn -> corn.height != null && corn.weight != null && corn.plot == Orientation.East)
                .forEach(corn -> {
                    serie1.add(corn.weight, corn.height);
                    x[corn.id-1] = corn.weight;
                    y[corn.id-1] = corn.height;
                });

            dataSet.addSeries(serie1.getKey(), serie1.toArray());

            JFreeChart scatterChart = ChartFactory.createScatterPlot("Masse/Hauteur", "Masse", "Hauteur", dataSet);

            scatterChart.setBackgroundPaint(Color.white);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(scatterChart.createBufferedImage(700, 400), "png", byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            graph5Image.setImage(new javafx.scene.image.Image(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
            double correlation = Statistics.getCorrelation(x, y);
            answer5Label.setText("Le coefficient de correlation est de " + correlation + ". On peut donc dire qu'il y a une "
                + (correlation > 0.85 ? "tres forte " : "faible")
                + "correlation entre la masse et la taille des pieds");
        }


        // QUESTION 6
        {
            DefaultXYDataset dataSet = new DefaultXYDataset();
            XYSeries serie1 = new XYSeries("Nombre grain/Hauteur");
            Number x[] = new Number[app.getData().getId().size()];
            Number y[] = new Number[app.getData().getId().size()];

            app.getData().stream()
                .filter(corn -> corn.height != null && corn.weight != null)
                .forEach(corn -> {
                    serie1.add(corn.grainCount, corn.height);
                    x[corn.id-1] = corn.grainCount;
                    y[corn.id-1] = corn.height;
                });

            dataSet.addSeries(serie1.getKey(), serie1.toArray());

            JFreeChart scatterChart = ChartFactory.createScatterPlot("Nombre grain/Hauteur", "Nombre de grain", "Hauteur", dataSet);

            scatterChart.setBackgroundPaint(Color.white);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(scatterChart.createBufferedImage(700, 400), "png", byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            graph6Image.setImage(new javafx.scene.image.Image(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
            double correlation = Statistics.getCorrelation(x, y);
            answer6Label.setText("Le coefficient de correlation est de " + correlation + ". On peut donc dire qu'il y a une "
                + (correlation > 0.85 ? "tres forte " : "faible ")
                + "correlation entre le nombre de grain et la taille des pieds");
        }


        // QUESTION 7
        {

        }
    }
}
