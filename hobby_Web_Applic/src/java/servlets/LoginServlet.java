package servlets;

import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.shopdb.Item;
import be.hepl.benbear.shopdb.Order;
import be.hepl.benbear.shopdb.OrderItem;
import be.hepl.benbear.shopdb.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {
    
    private Database database;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        Database.Driver.ORACLE.load();
        database = new SQLDatabase();
        database.registerClass(
            Item.class,
            Order.class,
            OrderItem.class,
            User.class
        );
        database.connect("jdbc:oracle:thin:@178.32.41.4:8080:xe", "dbshop", "bleh");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try (PrintWriter out = response.getWriter()) {
            String type = request.getParameter("type");
            
            switch(type) {
                case "Login":
                {
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    boolean valid = false;
                    
                    Optional<User> userOpt;
                    User user = null;
                    try {
                        userOpt = database.table(User.class).byId(username).get();
                        if(userOpt.isPresent()) {
                            System.out.println("User is present");
                            user = userOpt.get();
                            System.out.println("Retrieved used");
                            if(user.getPassword().equals(password)) {
                                valid = true;
                            }
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    response.setContentType("text/html;charset=UTF-8");
                    if (!valid) {
                        System.out.println("Sending login KO");
                        out.write("KOLogin");
                    } else {
                        System.out.println("Sending login OK");                        
                        out.write("OKLogin");
                    }
                    break;
                }
                case "RegistrationStep1":
                {
                    String username = request.getParameter("username");
                    boolean exists = true;
                    
                    Optional<User> userOpt;
                    User user = null;
                    try {
                        userOpt = database.table(User.class).byId(username).get();
                        if(!userOpt.isPresent()) {
                            exists = false;
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    response.setContentType("text/html;charset=UTF-8");
                    if (exists) {
                        out.write("KORegistration");
                    } else {
                        out.write("OKRegistration");
                    }
                    break;
                }
                case "RegistrationStep2":
                {
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    String email = request.getParameter("email");
                    database.table(User.class).insert(new User(username, password, email.equals("") ? null : email));
                    out.write("OKRegistration");
                    break;
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
