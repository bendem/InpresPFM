package be.hepl.benbear.boatapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {

    private FrameLayout layoutGraph;
    private ContainerMoveDAO containerMoveDAO;

    public static GraphFragment newInstance() {
        GraphFragment fragment = new GraphFragment();
        return fragment;
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
        layoutGraph = ((FrameLayout)view.findViewById(R.id.layoutGraph));
        setUpGraph();
        return view;
    }

    private void setUpGraph() {
        try {
            containerMoveDAO.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<Date, Long> dataGraph11 = containerMoveDAO.getCountMoveDay(ContainerMoveSQLiteHelper.MoveType.IN);
        Map<Date, Long> dataGraph12 = containerMoveDAO.getCountMoveDay(ContainerMoveSQLiteHelper.MoveType.OUT);
//        Map<String, Map<String, Integer>> dataGraph21 = containerMoveDAO.getCountMovePerDestinationPerWeek(ContainerMoveSQLiteHelper.MoveType.IN);
//        Map<String, Map<String, Integer>> dataGraph22 = containerMoveDAO.getCountMovePerDestinationPerWeek(ContainerMoveSQLiteHelper.MoveType.OUT);
        containerMoveDAO.close();

        TimeSeries series11 = new TimeSeries("Container loaded per day");
        TimeSeries series12 = new TimeSeries("Container unloaded per day");
        XYMultipleSeriesDataset series1 = new XYMultipleSeriesDataset();
        series1.addSeries(series11);
        series1.addSeries(series12);

        for (Date date : dataGraph11.keySet()) {
            series11.add(date, dataGraph11.get(date));
        }
        for (Date date : dataGraph12.keySet()) {
            series12.add(date, dataGraph12.get(date));
        }

        XYSeriesRenderer renderer11 = new XYSeriesRenderer();
        renderer11.setLineWidth(1);
        renderer11.setColor(Color.RED);
//        renderer11.setDisplayBoundingPoints(true);

        XYSeriesRenderer renderer12 = new XYSeriesRenderer();
        renderer12.setLineWidth(1);
        renderer12.setColor(Color.BLUE);
//        renderer12.setDisplayBoundingPoints(true);

        XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();
        renderer1.addSeriesRenderer(renderer11);
        renderer1.addSeriesRenderer(renderer12);

        renderer1.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        renderer1.setPanEnabled(false, false);
        renderer1.setYAxisMax(10);
        renderer1.setYAxisMin(0);
        renderer1.setBackgroundColor(Color.BLACK);

        GraphicalView gv1 = ChartFactory.getBarChartView(getActivity(), series1, renderer1, BarChart.Type.DEFAULT);

        layoutGraph.addView(gv1);
    }
}
