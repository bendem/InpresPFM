package be.hepl.benbear.reservationservlet;

import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.Table;
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

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        database.connect("jdbc:oracle:thin:@178.32.41.4:8080:xe", "dbtraffic", "bleh");
    }

    public void destroy() {
        try {
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpSession sess = req.getSession();
        Table<Parc> tableParc = database.table(Parc.class);
        Table<Reservation> tableReservation = database.table(Reservation.class);
        Object logged = sess.getAttribute("logged");
        String resId;
        if (logged != null)  {
            // TODO select * from parcs where container_id is null and (x,y) NOT IN (select X,Y from RESERVATIONS);
            Optional<Parc> parc = tableParc.findOne(DBPredicate.of("container_id", null)).get(5, TimeUnit.SECONDS);// ^ What it should do
            Parc plswork;
            if (parc.isPresent()) {
                plswork = parc.get();
                resId = "E"+ Date.valueOf(req.getParameter("dateArrival"))+plswork.getX()+plswork.getY();
                tableReservation.insert(new Reservation(plswork.getX(), plswork.getY(), Date.valueOf(req.getParameter("dateArrival")), req.getParameter("destination"), resId)).get();
                sendConfirmation(req, resp, resId);
            } else {
                System.out.println(parc);
            }

        } else {
            resp.sendRedirect("login.html");
        }
    }

    private void sendConfirmation(HttpServletRequest req, HttpServletResponse resp, String resId) {

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
