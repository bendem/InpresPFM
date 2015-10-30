package be.hepl.benbear.boatapp;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class GraphFragment extends Fragment {

    private FrameLayout layoutGraph;
    private ContainerMoveDAO containerMoveDAO;

    public static GraphFragment newInstance() {
        return new GraphFragment();
    }

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        containerMoveDAO = new ContainerMoveDAO(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_hist_day, container, false);
        layoutGraph = ((FrameLayout) view.findViewById(R.id.layoutGraph));
        setUpGraph();
        return view;
    }

    private void setUpGraph() {
        try {
            containerMoveDAO.open();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        Map<Date, Integer> dataGraph11 = containerMoveDAO.getCountMoveDay(ContainerMoveSQLiteHelper.MoveType.IN);
        Map<Date, Integer> dataGraph12 = containerMoveDAO.getCountMoveDay(ContainerMoveSQLiteHelper.MoveType.OUT);
//        Map<String, Map<String, Integer>> dataGraph21 = containerMoveDAO.getCountMovePerDestinationPerWeek(ContainerMoveSQLiteHelper.MoveType.IN);
//        Map<String, Map<String, Integer>> dataGraph22 = containerMoveDAO.getCountMovePerDestinationPerWeek(ContainerMoveSQLiteHelper.MoveType.OUT);
        containerMoveDAO.close();

        XYSeries series11 = new TimeSeries("Container loaded per day");
        XYSeries series12 = new TimeSeries("Container unloaded per day");
        XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset series1 = new XYMultipleSeriesDataset();
        series1.addSeries(series11);
        series1.addSeries(series12);

        int maxserie1 = 0;
        int i = 0;
        for (Date date : dataGraph11.keySet()) {
            maxserie1 = (maxserie1 > dataGraph11.get(date) ? maxserie1 : dataGraph11.get(date));
            series11.add(i, dataGraph11.get(date));
            renderer1.addXTextLabel(i, ContainerMoveDAO.DATE_FORMAT.format(date) + "              ");
            i++;
        }

        int maxserie2 = 0;
        int j = 0;
        for (Date date : dataGraph12.keySet()) {
            maxserie2 = (maxserie2 > dataGraph12.get(date) ? maxserie2 : dataGraph12.get(date));
            series12.add(j, dataGraph12.get(date));
            renderer1.addXTextLabel(j, ContainerMoveDAO.DATE_FORMAT.format(date) + "              ");
            j++;
        }

        XYSeriesRenderer renderer11 = new XYSeriesRenderer();
        renderer11.setLineWidth(1);
        renderer11.setColor(Color.CYAN);
        renderer11.setChartValuesSpacing(1);
//        renderer11.setDisplayBoundingPoints(true);

        XYSeriesRenderer renderer12 = new XYSeriesRenderer();
        renderer12.setLineWidth(1);
        renderer12.setColor(Color.BLUE);
        renderer12.setChartValuesSpacing(1);
//        renderer12.setDisplayBoundingPoints(true);

        renderer1.addSeriesRenderer(renderer11);
        renderer1.addSeriesRenderer(renderer12);

        renderer1.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        renderer1.setXAxisMax(i > j ? i : j);
        renderer1.setYAxisMax(maxserie1 > maxserie2 ? maxserie1 : maxserie2);
        renderer1.setYAxisMin(0);
        renderer1.setXAxisMin(-1);
        renderer1.setXLabelsAngle(-45);
        renderer1.setXLabelsAlign(Paint.Align.CENTER);
        renderer1.setYLabelsAlign(Paint.Align.LEFT);
        renderer1.setXLabelsColor(Color.BLACK);

        GraphicalView gv1 = ChartFactory.getBarChartView(getActivity(), series1, renderer1, BarChart.Type.DEFAULT);

        layoutGraph.addView(gv1);
    }
}
