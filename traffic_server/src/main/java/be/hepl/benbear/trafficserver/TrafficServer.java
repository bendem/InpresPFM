package be.hepl.benbear.trafficserver;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.net.Server;
import be.hepl.benbear.commons.protocol.Packet;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import be.hepl.benbear.protocol.tramap.*;
import be.hepl.benbear.trafficdb.*;
import be.hepl.benbear.trafficdb.Movement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TrafficServer extends Server<DataInputStream, DataOutputStream> {

    private final Config config;
    private final ProtocolHandler protocolHandler;
    private final SQLDatabase database;

    public TrafficServer(Config config) {
        super(
            UncheckedLambda.supplier(() -> InetAddress.getByName(config.getString("trafficserver.host").orElse("localhost"))).get(),
            config.getInt("trafficserver.port").orElse(31065),
            Thread::new,
            Executors.newSingleThreadExecutor(),
            DataInputStream::new,
            DataOutputStream::new
        );
        this.config = config;

        Database.Driver.ORACLE.load();

        database = new SQLDatabase();
        database.registerClass(User.class);
        database.registerClass(Company.class);
        database.registerClass(Movement.class);
        database.registerClass(Container.class);
        database.registerClass(Reservation.class);
        database.registerClass(ReservationsContainers.class);
        database.registerClass(FreeParc.class);
        database.registerClass(MovementsLight.class);
        database.connect(
            config.getString("jdbc.url").get(),
            config.getString("jdbc.trafficdb.user").get(),
            config.getString("jdbc.trafficdb.password").get());

        protocolHandler = new ProtocolHandler();
        protocolHandler.registerPacket(LoginPacket.ID, LoginPacket.class);
        protocolHandler.registerPacket(LoginResponsePacket.ID, LoginResponsePacket.class);
        protocolHandler.registerPacket(InputLorryPacket.ID, InputLorryPacket.class);
        protocolHandler.registerPacket(InputLorryResponsePacket.ID, InputLorryResponsePacket.class);
        protocolHandler.registerPacket(InputLorryWithoutReservationPacket.ID, InputLorryWithoutReservationPacket.class);
        protocolHandler.registerPacket(InputLorryWithoutReservationResponsePacket.ID, InputLorryWithoutReservationResponsePacket.class);
        protocolHandler.registerPacket(ListOperationsPacket.ID, ListOperationsPacket.class);
        protocolHandler.registerPacket(ListOperationsResponsePacket.ID, ListOperationsResponsePacket.class);
        protocolHandler.registerPacket(LogoutPacket.ID, LogoutPacket.class);
        protocolHandler.registerPacket(LogoutResponsePacket.ID, LogoutResponsePacket.class);
        new ContainerPosition("d",0,0);
        new be.hepl.benbear.protocol.tramap.Movement(0,"o","o","o",0,0);
    }

    @Override
    protected void read(DataInputStream is, DataOutputStream os) throws IOException {
        Packet packet = protocolHandler.read(is);

        switch(packet.getId()) {
            case LoginPacket.ID:
                onLoginPacket((LoginPacket)packet, os);
                break;
            case InputLorryPacket.ID:
                onInputLorryPacket((InputLorryPacket)packet, os);
                break;
            case InputLorryWithoutReservationPacket.ID:
                onInputLorryWithoutReservationPacket((InputLorryWithoutReservationPacket)packet, os);
                break;
            case ListOperationsPacket.ID:
                onListOperationPacket((ListOperationsPacket)packet, os);
                break;
            case LogoutPacket.ID:
                onLogoutPacket((LogoutPacket) packet, os);
                break;
        }
    }

    private void onLoginPacket(LoginPacket packet, DataOutputStream os) throws IOException {
        if(check(packet.getUsername(), packet.getPassword())) {
            protocolHandler.write(os, new LoginResponsePacket(true, "Good"));
        } else {
            Log.w("Connection failure from %s", packet.getUsername());
            protocolHandler.write(os, new LoginResponsePacket(false, "User/password not found"));
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

    private void onInputLorryPacket(InputLorryPacket packet, DataOutputStream os) throws IOException {
        List<ReservationsContainers> listRes;

        try {
            listRes = database.table(ReservationsContainers.class).find(DBPredicate.of("reservation_id", packet.getReservationId())).get().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Error retrieving reservation", e);
            protocolHandler.write(os, new InputLorryResponsePacket(false, "Reservation number " + packet.getReservationId() + "not found", new ContainerPosition[0]));
            return;
        }
        if (!listRes.stream().map(ReservationsContainers::getContainerId).collect(Collectors.toList()).containsAll(packet.getContainerIds())) {
            protocolHandler.write(os, new InputLorryResponsePacket(false, "One of the ids is not part of the reservation", new ContainerPosition[0]));
            return;
        }
        protocolHandler.write(os, new InputLorryResponsePacket(true, "Good", listRes.stream()
            .filter(r -> packet.getContainerIds().contains(r.getContainerId()))
            .map(r -> new ContainerPosition(r.getContainerId(), r.getX(), r.getY()))
            .toArray(ContainerPosition[]::new)
        ));
    }

    private void onInputLorryWithoutReservationPacket(InputLorryWithoutReservationPacket packet, DataOutputStream os) throws IOException {
        List<FreeParc> freeParc;
        try {
            freeParc = database.table(FreeParc.class).find().get().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Error retrieving reservation", e);
            protocolHandler.write(os, new InputLorryWithoutReservationResponsePacket(false, "No free space in the parc", new ContainerPosition[0]));
            return;
        }
        if (freeParc.size() < packet.getContainerIds().size()) {
            protocolHandler.write(os, new InputLorryWithoutReservationResponsePacket(false, "Not enough free space in the parc", new ContainerPosition[0]));
            return;
        }

        Iterator<FreeParc> itParc = freeParc.iterator();
        Iterator<String> itCont = packet.getContainerIds().iterator();
        ContainerPosition contPos[] = new ContainerPosition[packet.getContainerIds().size()];
        int i = 0;
        while(itCont.hasNext()) {
            String contid = itCont.next();
            FreeParc fp = itParc.next();
            contPos[i] = new ContainerPosition(contid, fp.getX(), fp.getY());
            i++;
        }

        protocolHandler.write(os, new InputLorryWithoutReservationResponsePacket(true, "Good", contPos));
    }

    private void onListOperationPacket(ListOperationsPacket packet, DataOutputStream os) throws IOException {
        List<MovementsLight> movements;
        try {
            movements = database.table(MovementsLight.class)
                .find(DBPredicate.of(packet.getType().equals(ListOperationsPacket.Type.Society.toString()) ? "name" : "city", packet.getCriteria()))
                .get().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Error retrieving reservation", e);
            protocolHandler.write(os, new ListOperationsResponsePacket(false, "No movements for the provided criteria", new be.hepl.benbear.protocol.tramap.Movement[0]));
            return;
        }
        protocolHandler.write(os, new ListOperationsResponsePacket(true, "Good", movements.stream().map(
            m -> new be.hepl.benbear.protocol.tramap.Movement(m.getMovementId(), m.getContainerId(), m.getDestination(), m.getCompanyName(), m.getDateArrival().getTime(), m.getDateDeparture().getTime())
        ).toArray(be.hepl.benbear.protocol.tramap.Movement[]::new)));
    }

    private void onLogoutPacket(LogoutPacket packet, DataOutputStream os) throws IOException {
        protocolHandler.write(os, new LogoutResponsePacket(true, "Good"));
    }

    @Override
    protected void onClose(SocketChannel channel, Exception e) {
        if(e != null) {
            Log.e("Channel error %s", e, channel);
        }
    }
}
