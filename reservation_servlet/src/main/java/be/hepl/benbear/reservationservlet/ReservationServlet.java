package be.hepl.benbear.reservationservlet;

import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.trafficdb.Destination;
import be.hepl.benbear.trafficdb.Parc;
import be.hepl.benbear.trafficdb.Reservation;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ReservationServlet extends HttpServlet {

    private Database database;

    @Override
    public void init() {
        database = new SQLDatabase();
        database.registerClass(Reservation.class);
        database.registerClass(Parc.class);
        database.registerClass(Destination.class);

        try {
            Class.forName(getInitParameter("jdbcDriver"));
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        database.connect(getInitParameter("jdbcConnection"), getInitParameter("jdbcUsername"), getInitParameter("jdbcPassword"));
    }

    public void destroy() {
        try {
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpSession session = req.getSession();
        Table<Parc> parcTable = database.table(Parc.class);
        Table<Reservation> reservationTable = database.table(Reservation.class);
        String resId;
        // TODO This would be faster db side
        List<Reservation> reservations = reservationTable.find().get().collect(Collectors.toList());
        Optional<Parc> qparc = parcTable.find(DBPredicate.of("container_id", null)).get()
            .filter(p -> reservations.stream().noneMatch(r -> p.getX() == r.getX() && p.getY() == r.getY()))
            .findFirst();

        if(qparc.isPresent()) {
            Parc parc = qparc.get();
            resId = "R"+ Date.valueOf(req.getParameter("dateArrival"))+parc.getX()+parc.getY();
            reservationTable.insert(new Reservation(parc.getX(), parc.getY(), Date.valueOf(req.getParameter("dateArrival")), req.getParameter("destination"), resId)).get();
            sendConfirmation(req, resp, resId, parc.getX(), parc.getY());
        } else {
            session.setAttribute("noSpace", true);
            resp.sendRedirect("ServletLog");
        }
    }

    private void sendConfirmation(HttpServletRequest req, HttpServletResponse resp, String resId, int x, int y) throws IOException, ExecutionException, InterruptedException {
        HttpSession session = req.getSession();

        Table<Destination> destinationTable = database.table(Destination.class);

        session.setAttribute("reservationid", resId);
        session.setAttribute("positionx", x);
        session.setAttribute("positiony", y);
        session.setAttribute("datereservation", req.getParameter("dateArrival"));
        session.setAttribute("destination", destinationTable.byId(req.getParameter("destination")).get().get().getCity());

        resp.sendRedirect("Reservation.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
