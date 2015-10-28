package be.hepl.benbear.reservationservlet;

import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.trafficdb.Destination;
import be.hepl.benbear.trafficdb.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    private Database database;

    @Override
    public void init() {
        database = new SQLDatabase();
        database.registerClass(Destination.class);
        database.registerClass(User.class);

        try {
            Class.forName(getInitParameter("jdbcDriver"));
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        database.connect(
            getInitParameter("jdbcConnection"),
            getInitParameter("jdbcUsername"),
            getInitParameter("jdbcPassword")
        );
    }

    public void destroy() {
        try {
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (req.getSession().getAttribute("logged") != null) {
            proceedToReservation(req, resp);
            return;
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(username == null || password == null) {
            resp.sendRedirect("login.html");
            return;
        }

        Table<User> table = database.table(User.class);
        if ("on".equals(req.getParameter("newuser"))) {
            table.insert(new User(0, username, password)).get();
        } else {
            Optional<User> user = table.findOne(DBPredicate.of("username", username)).get(5, TimeUnit.SECONDS);
            if(!user.isPresent() || !user.get().getPassword().equals(password)) {
                resp.sendRedirect("login.html");
                return;
            }
        }

        proceedToReservation(req, resp);
    }

    protected void proceedToReservation(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession sess = req.getSession();
        Object noSpace = sess.getAttribute("noSpace");
        PrintWriter out = resp.getWriter();
        Table<Destination> table = database.table(Destination.class);

        resp.setContentType("text/html;charset=UTF-8");
        sess.setAttribute("logged", true);

        out.println("<html><head><title>Reservation</title></head><body>");

        if (noSpace == null) {
            out.println("<p>Reservation form</p>");
            out.println("<form method=\"post\" action=\"ServletRes\">");
            out.println("<p>Arrival date : <input type=\"date\" name=\"dateArrival\"></p>");

            out.println("<p>Destination : <select name=\"destination\">");
            table.find().get().forEach(dest -> out.printf("<option value=\"%d\">%s</option>", dest.getDestinationId(), dest.getCity()));
            out.println("</select></p>");

            out.println("<input type=\"submit\">");
            out.println("</form>");
        } else {
            out.println("<p>No more space available</p>");
        }
        out.println("</body></html>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
