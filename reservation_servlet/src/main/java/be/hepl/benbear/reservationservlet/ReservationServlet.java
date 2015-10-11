package be.hepl.benbear.reservationservlet;

import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.trafficdb.Destination;
import be.hepl.benbear.trafficdb.Parc;
import be.hepl.benbear.trafficdb.Reservation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReservationServlet extends HttpServlet {

    private Database database;

    @Override
    public void init() {
        database = new Database();
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
        Object logged = session.getAttribute("logged");
        String resId;
        if (logged != null)  {
            // TODO select * from parcs where container_id is null and (x,y) NOT IN (select X,Y from RESERVATIONS);
            Optional<Parc> qparc = parcTable.findOne(DBPredicate.of("container_id", null)).get(5, TimeUnit.SECONDS);// ^ What it should do
            if(qparc.isPresent()) {
                Parc parc = qparc.get();
                resId = "R"+ Date.valueOf(req.getParameter("dateArrival"))+parc.getX()+parc.getY();
                reservationTable.insert(new Reservation(parc.getX(), parc.getY(), Date.valueOf(req.getParameter("dateArrival")), req.getParameter("destination"), resId)).get();
                sendConfirmation(req, resp, resId, parc.getX(), parc.getY());
            } else {
                session.setAttribute("noSpace", true);
                resp.sendRedirect("ServletLog");
            }
        } else {
            resp.sendRedirect("login.html");
        }
    }

    private void sendConfirmation(HttpServletRequest req, HttpServletResponse resp, String resId, int x, int y) throws IOException, ExecutionException, InterruptedException {
        HttpSession session = req.getSession();

        Table<Destination> destinationTable = database.table(Destination.class);

        session.setAttribute("reservationid", resId);
        session.setAttribute("positionx", x);
        session.setAttribute("positiony", y);
        session.setAttribute("datereservation", req.getParameter("dateArrival"));
        session.setAttribute("destination", destinationTable.byId(Throwable::printStackTrace, req.getParameter("destination")).get().get().getCity());

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
