package be.hepl.benbear.trafficserver;

import be.hepl.benbear.boomap.*;
import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.trafficdb.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrafficBoomapServer extends Server<DataInputStream, DataOutputStream> {

    private final Config config;
    private final ProtocolHandler protocolHandler;
    private final SQLDatabase database;

    public TrafficBoomapServer(Config config) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(config.getString("trafficserver.host").orElse("localhost"))).get(),
            config.getInt("trafficserver.boomapport").orElse(31066),
            Thread::new,
            Executors.newSingleThreadExecutor(),
            DataInputStream::new,
            DataOutputStream::new
        );
        this.config = config;

        Database.Driver.ORACLE.load();

        database = new SQLDatabase();
        database.registerClass(
            User.class,
            Company.class,
            Movement.class,
            Container.class,
            Reservation.class,
            ReservationsContainers.class,
            FreeParc.class,
            MovementsLight.class,
            ContainerLeaving.class,
            ContainerIncoming.class);
        database.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.trafficdb.user").get(),
            config.getString("jdbc.trafficdb.password").get());

        protocolHandler = new ProtocolHandler();
        protocolHandler.registerPacket(GetListPacket.ID, GetListPacket.class);
        protocolHandler.registerPacket(GetListResponsePacket.ID, GetListResponsePacket.class);
        protocolHandler.registerPacket(GetXYPacket.ID, GetXYPacket.class);
        protocolHandler.registerPacket(GetXYResponsePacket.ID, GetXYResponsePacket.class);
        protocolHandler.registerPacket(LoginContPacket.ID, LoginContPacket.class);
        protocolHandler.registerPacket(LoginContResponsePacket.ID, LoginContResponsePacket.class);
        protocolHandler.registerPacket(SendWeightPacket.ID, SendWeightPacket.class);
        protocolHandler.registerPacket(SendWeightResponsePacket.ID, SendWeightResponsePacket.class);
        protocolHandler.registerPacket(SignalDepPacket.ID, SignalDepPacket.class);
        protocolHandler.registerPacket(SignalDepResponsePacket.ID, SignalDepResponsePacket.class);
    }

    @Override
    protected void read(DataInputStream is, DataOutputStream os) throws IOException {
        Packet packet = protocolHandler.read(is);

        switch(packet.getId()) {
            case GetListPacket.ID:
                onGetListPacket(os, (GetListPacket) packet);
                break;
            case GetXYPacket.ID:
                onGetXYPacket(os, (GetXYPacket) packet);
                break;
            case LoginContPacket.ID:
                onLoginContPacket(os, (LoginContPacket) packet);
                break;
            case SendWeightPacket.ID:
                onSendWeightPacket(os, (SendWeightPacket) packet);
                break;
            case SignalDepPacket.ID:
                onSignalDepPacket(os, (SignalDepPacket) packet);
                break;
        }
    }

    private void onLoginContPacket(DataOutputStream os, LoginContPacket packet) throws IOException {
        if(check(packet.getUsername(), packet.getPassword())) {
            protocolHandler.write(os, new LoginContResponsePacket(true, "Good"));
        } else {
            Log.w("Connection failure from %s", packet.getUsername());
            protocolHandler.write(os, new LoginContResponsePacket(false, "User/password not found"));
        }
    }


    private boolean check(String username, String password) {
        Optional<User> staff;
        try {
            staff = database.table(User.class).findOne(DBPredicate.of("username", username)).get();
        } catch(InterruptedException | ExecutionException e) {
            Log.e("Error retrieving user", e);
            return false;
        }

        return staff.isPresent() && staff.get().getPassword().equals(password);
    }

    private void onGetListPacket(DataOutputStream os, GetListPacket packet) throws IOException {
        Stream<ContainerLeaving> conts;
        Position[] positions;
        try {
            conts = database.table(ContainerLeaving.class).find(DBPredicate.of("city", packet.getDestination())).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Error retrieving leaving containers", e);
            return;
        }

        positions = conts.limit(packet.getCount()).map(elem -> new Position(elem.getX(), elem.getY())).toArray(Position[]::new);

        if (positions.length > 0) {
            protocolHandler.write(os, new GetListResponsePacket(true, "", positions));
        } else {
            protocolHandler.write(os, new GetListResponsePacket(false, "No containers found for that destination", positions));
        }
    }

    private void onGetXYPacket(DataOutputStream os, GetXYPacket packet) throws IOException {
        List<ContainerIncoming> containerIncomings;
        List<Position> positions = new ArrayList<>();
        try {
            containerIncomings = database.table(ContainerIncoming.class).find().get().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Error retrieving leaving containers", e);
            return;
        }

        containerIncomings.forEach(elem -> {
            if(packet.getContainerIds().contains(elem.getContainerId())) {
                positions.add(new Position(elem.getX(), elem.getY()));
            }
        });

        if (positions.size() != packet.getContainerIds().size()){
            protocolHandler.write(os, new GetXYResponsePacket(false, "Some of the containers are unexpected", new Position[0]));
        } else {
            protocolHandler.write(os, new GetXYResponsePacket(true, null, positions.stream().toArray(Position[]::new)));
        }

        database.table(ReservationsContainers.class).delete(DBPredicate.of("container_id", "(\"" + positions.stream().map(String::valueOf).collect(Collectors.joining("\", \"")) + "\")", "in"));
    }

    private void onSendWeightPacket(DataOutputStream os, SendWeightPacket packet) throws IOException {
        for (int i = 0; i < packet.getContainerIds().length; i++) {
            database.table(Parc.class).update(new Parc(packet.getContainerPositions()[i].getX(),
                packet.getContainerPositions()[i].getY(),
                packet.getContainerIds()[i]));
            database.table(Movement.class).insert(new Movement(0, packet.getContainerIds()[i],
                1, // TODO SAVE THE COMPANY FROM GETXY
                null, // TODO SAVE THE TRANSPORTER FROM GETXY
                null,
                new Date(Instant.now().getEpochSecond()),
                null,
                packet.getContainerWeights()[i],
                1 // TODO SAVE THE DESTINATIONS FROM GETXY
                ));
        }

        protocolHandler.write(os, new SendWeightResponsePacket(true, "X"));
    }

    private void onSignalDepPacket(DataOutputStream os, SignalDepPacket packet) throws IOException {
        List<Parc> parcs;
        List<Movement> movements;
        try {
            parcs = database.table(Parc.class).find(DBPredicate.of("container_id",
                "(" + Arrays.stream(packet.getContainerIds())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")) + ")"))
                .get().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Error retrieving leaving containers", e);
            return;
        }

        parcs.forEach(elem -> database.table(Parc.class).update(new Parc(elem.getX(), elem.getY(), null)));

        try {
            movements = database.table(Movement.class).find(DBPredicate.of("container_id",
                "(" + Arrays.stream(packet.getContainerIds())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")) + ")")
                .and("date_departure", null, "is")).get().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Error retrieving movements", e);
            return;
        }

        movements.forEach(elem -> database.table(Movement.class)
            .update(new Movement(elem.getMovementId(), elem.getContainerId(), elem.getCompanyId(), elem.getTransporterIdIn(),
            elem.getTransporterIdOut(), elem.getDateArrival(), new Date(Instant.now().getEpochSecond()), elem.getWeight(), elem.getDestinationId())));

        protocolHandler.write(os, new SignalDepResponsePacket(true, "X"));
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        if(e != null) {
            Log.e("Channel error %s", e, channel);
        }
    }
}
