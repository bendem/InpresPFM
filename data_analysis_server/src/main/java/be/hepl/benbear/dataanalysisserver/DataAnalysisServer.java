package be.hepl.benbear.dataanalysisserver;

import be.hepl.benbear.accounting_db.Staff;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.security.Digestion;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.decisiondb.AnovaStats;
import be.hepl.benbear.decisiondb.ConformityStats;
import be.hepl.benbear.decisiondb.DescriptiveStats;
import be.hepl.benbear.decisiondb.HomogeneityStats;
import be.hepl.benbear.pidep.*;
import be.hepl.benbear.trafficdb.*;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class DataAnalysisServer extends Server<ObjectInputStream, ObjectOutputStream> {

    private final SQLDatabase accountingDb;
    private final SQLDatabase trafficDb;
    private final SQLDatabase decisionDb;
    private final Set<UUID> sessions;

    public DataAnalysisServer(Config config) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(config.getString("dataanalysisserver.host").orElse("localhost"))).get(),
            config.getInt("dataanalysisserver.port").orElse(31067),
            Thread::new,
            Executors.newFixedThreadPool(2),
            UncheckedLambda.function(ObjectInputStream::new),
            UncheckedLambda.function(os -> {
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.flush();
                return oos;
            })
        );

        accountingDb = new SQLDatabase();
        accountingDb.registerClass(Staff.class);
        accountingDb.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.accounting.user").get(),
            config.getString("jdbc.accounting.password").get());

        trafficDb = new SQLDatabase();
        trafficDb.registerClass(Company.class,
            Container.class,
            Destination.class,
            FreeParc.class,
            Movement.class,
            MovementsLight.class,
            Parc.class,
            Reservation.class,
            ReservationsContainers.class,
            Transporter.class,
            User.class,
            ContainerPerDestMonth.class,
            ContainerPerDestYear.class,
            ContainerPerDestQuarter.class);
        trafficDb.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.trafficdb.user").get(),
            config.getString("jdbc.trafficdb.password").get());

        decisionDb = new SQLDatabase();
        decisionDb.registerClass(
            DescriptiveStats.class,
            ConformityStats.class,
            HomogeneityStats.class,
            AnovaStats.class
        );
        decisionDb.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.decisiondb.user").get(),
            config.getString("jdbc.decisiondb.password").get());

        sessions = new CopyOnWriteArraySet<>();
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        if(!(e instanceof EOFException)) {
            Log.e("%s errored", e, channel);
        }
    }

    @Override
    protected void read(ObjectInputStream is, ObjectOutputStream os) throws IOException {
        Packet packet;

        try {
            packet = (Packet) is.readObject();
        } catch(ClassNotFoundException | ClassCastException e) {
            Log.e("Invalid packet received", e);
            return;
        }

        if(packet instanceof AuthenticatedPacket) {
            if(!sessions.contains(((AuthenticatedPacket) packet).getSession())) {
                os.writeObject(new ErrorPacket("Invalid session"));
                os.flush();
                return;
            }
        }

        switch(packet.getId()) {
            case Login:
                Log.d("Received Login");
                login(os, (LoginPacket) packet);
                break;
            case GetContainerDescriptiveStatistic:
                Log.d("Received GetContainerDescriptiveStatistic");
                containerDescriptiveStatistic(os, (GetContainerDescriptiveStatisticPacket) packet);
                break;
            case GetContainerPerDestinationGraph:
                Log.d("Received GetContainerPerDestinationGraph");
                containerPerDestinationGraph(os, (GetContainerPerDestinationGraphPacket) packet);
                break;
            case GetContainerPerDestinationPerQuarterGraph:
                Log.d("Received GetContainerPerDestinationPerQuarterGraph");
                containerPerDestinationPerQuarter(os, (GetContainerPerDestinationPerQuarterGraphPacket) packet);
                break;
            case GetStatInferConformityTest:
                Log.d("Received GetStatInferConformityTest");
                statInferConformityTest(os, (GetStatInferConformityTestPacket) packet);
                break;
            case GetStatInferHomogeneityTest:
                Log.d("Received GetStatInferHomogeneityTest");
                statInferHomogeneityTest(os, (GetStatInferHomogeneityTestPacket) packet);
                break;
            case GetStatInferAnovaTest:
                Log.d("Received GetStatInferAnovaTest");
                statInferAnovaTest(os, (GetStatInferAnovaTestPacket) packet);
                break;
            default:
                Log.e("Unhandled packet: %s", packet);
        }
        os.flush();
    }

    private void login(ObjectOutputStream os, LoginPacket p) throws IOException {
        Optional<Staff> user;
        try {
            user = accountingDb.table(Staff.class)
                .findOne(DBPredicate.of("login", p.getUsername())).get();
        } catch(InterruptedException | ExecutionException e) {
            Log.e("Failed to retrieve user %s", e, p.getUsername());
            os.writeObject(new ErrorPacket("Internal error"));
            return;
        }

        if(!user.isPresent()) {
            os.writeObject(new ErrorPacket("Unknown user"));
            return;
        }

        if(Digestion.check(p.getDigest(), user.get().getPassword(), p.getTime(), p.getSalt())) {
            UUID uuid = UUID.randomUUID();
            sessions.add(uuid);
            os.writeObject(new LoginReponsePacket(uuid));
        } else {
            os.writeObject(new ErrorPacket("Password invalid"));
        }
    }

    private void containerDescriptiveStatistic(ObjectOutputStream os, GetContainerDescriptiveStatisticPacket packet) throws IOException {
        Stream<MovementsLight> movements;
        try {
            if (packet.getType() == GetContainerDescriptiveStatisticPacket.Type.IN) {
                movements = trafficDb.table(MovementsLight.class)
                    .find(DBPredicate.of("date_departure", null, "is")).get();
            } else {
                movements = trafficDb.table(MovementsLight.class)
                    .find(DBPredicate.of("date_departure", null, "is not")).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(System.out);
            os.writeObject(new ErrorPacket("Failed to retrieve movements"));
            return;
        }

        double[] weights = movements.limit(packet.getSampleSize()).mapToDouble(MovementsLight::getWeight).toArray();

        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(weights);

        double mean = descriptiveStatistics.getMean();
        double median = descriptiveStatistics.getPercentile(50);
        double stddev = descriptiveStatistics.getStandardDeviation();

        os.writeObject(new GetContainerDescriptiveStatisticResponsePacket(
            descriptiveStatistics.getMean(),
            StatUtils.mode(descriptiveStatistics.getValues()),
            descriptiveStatistics.getPercentile(50),
            descriptiveStatistics.getStandardDeviation())
        );

        decisionDb.table(DescriptiveStats.class).insert(new DescriptiveStats(
            0,
            mean,
            Arrays.stream(StatUtils.mode(descriptiveStatistics.getValues())).mapToObj(String::valueOf).collect(Collectors.joining(", ")),
            median,
            stddev,
            packet.getType().name(),
            packet.getSampleSize()
        ));
    }

    private void containerPerDestinationGraph(ObjectOutputStream os, GetContainerPerDestinationGraphPacket packet) throws IOException {
        String title;
        Stream<? extends ContainterPerDestination> conts;
        try {
            if (packet.getType() == GetContainerPerDestinationGraphPacket.Type.MONTHLY) {
                conts = trafficDb.table(ContainerPerDestMonth.class)
                    .find(DBPredicate.of("month", packet.getValue())).get();
                title = "Number of containers per destination for the month " + packet.getValue();
            } else {
                conts = trafficDb.table(ContainerPerDestYear.class)
                    .find(DBPredicate.of("year", packet.getValue())).get();
                title = "Number of containers per destination for the year " + packet.getValue();
            }
        } catch (InterruptedException | ExecutionException e) {
            os.writeObject(new ErrorPacket("Failed to retrieve movements"));
            Log.e("Failed to retrieve movements %s", e);
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        conts.forEach(e -> dataset.setValue(e.getCity(), e.getCount()));

        JFreeChart chart = ChartFactory.createPieChart(title , dataset);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(chart.createBufferedImage(700, 400), "png", byteArrayOutputStream);

        os.writeObject(new GetContainerPerDestinationGraphResponsePacket(byteArrayOutputStream.toByteArray()));
    }

    private void containerPerDestinationPerQuarter(ObjectOutputStream os, GetContainerPerDestinationPerQuarterGraphPacket packet) throws IOException {
        Stream<ContainerPerDestQuarter> conts;
        try {
            conts = trafficDb.table(ContainerPerDestQuarter.class)
                .find(DBPredicate.of("year", packet.getYear())).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Failed to retrieve movements %s", e);
            os.writeObject(new ErrorPacket("No containers in the database fitting the criteria"));
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        conts.forEach(e -> dataset.setValue((Number) e.getCount(), e.getCity(), e.getQuarter()));

        JFreeChart chart = ChartFactory.createBarChart("Number of containers", "Quarters", "Number", dataset);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(chart.createBufferedImage(700, 400), "png", byteArrayOutputStream);

        os.writeObject(new GetContainerPerDestinationPerQuarterGraphResponsePacket(byteArrayOutputStream.toByteArray()));
    }

    private void statInferConformityTest(ObjectOutputStream os, GetStatInferConformityTestPacket packet) throws IOException {
        Stream<MovementsLight> conts;
        double[] values;
        try {
            conts = trafficDb.table(MovementsLight.class)
                .find(DBPredicate.of("date_departure", null, "is not")).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Failed to retrieve movements %s", e);
            os.writeObject(new ErrorPacket("No containers in the database fitting the criteria"));
            return;
        }

        values = conts.limit(packet.getNumberOfElem())
            .mapToDouble(value -> (value.getDateDeparture().getDay() - value.getDateArrival().getDay())).toArray();

        double pvalue = TestUtils.tTest(10, values);
        boolean significant = TestUtils.tTest(10, values, 0.025);

        os.writeObject(new GetStatInferConformityTestResponsePacket(significant, pvalue));

        decisionDb.table(ConformityStats.class).insert(new ConformityStats(
            0,
            packet.getNumberOfElem(),
            pvalue,
            significant ? 1 : 0
        ));

    }

    private void statInferHomogeneityTest(ObjectOutputStream os, GetStatInferHomogeneityTestPacket packet) throws IOException {
        Stream<MovementsLight> data1;
        Stream<MovementsLight> data2;
        double[] values1;
        double[] values2;
        try {
            data1 = trafficDb.table(MovementsLight.class)
                .find(DBPredicate.of("city", packet.getFirstCity()).and("date_departure", null, "is not")).get();
            data2 = trafficDb.table(MovementsLight.class)
                .find(DBPredicate.of("city", packet.getSecondCity()).and("date_departure", null, "is not")).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Failed to retrieve movements %s", e);
            os.writeObject(new ErrorPacket("No containers in the database fitting the criteria"));
            return;
        }

        values1 = data1
            .limit(packet.getNumberOfElem())
            .mapToDouble(value -> (value.getDateDeparture().getDay() - value.getDateArrival().getDay()))
            .toArray();
        values2 = data2
            .limit(packet.getNumberOfElem())
            .mapToDouble(value -> (value.getDateDeparture().getDay() - value.getDateArrival().getDay()))
            .toArray();

        double pvalue = TestUtils.tTest(values1, values2);
        boolean significant = TestUtils.tTest(values1, values2, 0.025);

        os.writeObject(new GetStatInferHomogeneityTestResponsePacket(significant, pvalue));

        decisionDb.table(HomogeneityStats.class).insert(new HomogeneityStats(
            0,
            packet.getNumberOfElem(),
            packet.getFirstCity(),
            packet.getSecondCity(),
            pvalue,
            significant ? 1 : 0
        ));
    }

    private void statInferAnovaTest(ObjectOutputStream os, GetStatInferAnovaTestPacket packet) throws IOException {
        Stream<MovementsLight> data;
        try {
            data = trafficDb.table(MovementsLight.class)
                .find(DBPredicate.of("date_departure", null, "is not")).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Failed to retrieve movements %s", e);
            os.writeObject(new ErrorPacket("No containers in the database fitting the criteria"));
            return;
        }

        Map<String, List<Double>> values = new HashMap<>();

        data.forEach(mov -> values
            .computeIfAbsent(mov.getDestination(), k -> new ArrayList<>())
            .add((double) mov.getDateDeparture().getDay() - mov.getDateArrival().getDay()));

        List<double[]> dataLists = new ArrayList<>();
        values.forEach(
            (s, doubles) -> dataLists.add(
                doubles.stream()
                    .limit(packet.getNumberOfElem())
                    .mapToDouble(d -> d).toArray()));

        Log.d("%s", dataLists.stream().map(Arrays::toString).collect(Collectors.toList()));

        double pvalue = TestUtils.oneWayAnovaPValue(dataLists);
        boolean significant = TestUtils.oneWayAnovaTest(dataLists, 0.025);

        os.writeObject(new GetStatInferAnovaTestResponsePacket(significant, pvalue));

        decisionDb.table(AnovaStats.class).insert(new AnovaStats(
            0,
            packet.getNumberOfElem(),
            pvalue,
            significant ? 1 : 0
        ));
    }
}
