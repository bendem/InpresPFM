package be.hepl.benbear.reservationservlet;

import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.trafficdb.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginServlet extends HttpServlet {

    private Database database;

    @Override
    public void init() {
        database = new Database();
        database.registerClass(Destination.class);
        database.registerClass(User.class);

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
        Table<User> table = database.table(User.class);
        if ("on".equals(req.getParameter("newuser"))) {
            table.insert(new User(0, req.getParameter("username"), req.getParameter("password"))).get();
            proceedToReservation(req, resp, req.getParameter("username"));
        } else {
            User user = table.find(DBPredicate.of("username", req.getParameter("username"))).get(5, TimeUnit.SECONDS).findFirst().get();
            if(user.getPassword().equals(req.getParameter("password"))) {
                proceedToReservation(req, resp, user.getUsername());
            } else {
                // TODO KO => Display the same login page with an error message?
            }
        }
    }

    protected void proceedToReservation(HttpServletRequest req, HttpServletResponse resp, String username) throws IOException, ExecutionException, InterruptedException {
        HttpSession sess = req.getSession();

        sess.setAttribute("logged", true);
        sess.setAttribute("username", username);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<html><head><title>");
        out.println("Reservation");
        out.println("</title></head><body>");
        out.println("<p>Reservation form</p>");
        out.println("<form method=\"post\" action=\"ServletRes\">");
        out.println("<p>Arrival date : <input type=\"date\" name=\"dateArrival\"></p>");
        out.println("<p>Destination : </p><select name=\"destination\">");

        Table<Destination> table = database.table(Destination.class);
        table.find().get().forEach(row -> out.println("<option value=\"" + row.getDestinationId() + "\">" + row.getCity() + "</option>"));

        out.println("</select>");
        out.println("<input type=\"submit\" value=\"Submit\">");
        out.println("<html><head><title>");
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
