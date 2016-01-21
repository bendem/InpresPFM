package servlets;

import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.shopdb.Item;
import be.hepl.benbear.shopdb.Order;
import be.hepl.benbear.shopdb.OrderItem;
import be.hepl.benbear.shopdb.OrderReservation;
import be.hepl.benbear.shopdb.Reservation;
import be.hepl.benbear.shopdb.User;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class ShopServlet extends HttpServlet implements HttpSessionListener {

    private Database database;
    private int orderIdSeq = -1;
    
    protected synchronized int getNextOrderId() {
        if(orderIdSeq == -1){
            try {
                Optional<Order> orderOpt = getDatabase().table(Order.class).find().get()
                        .max((o1, o2) -> Integer.compare(o1.getOrderId(), o2.getOrderId()));
                if (orderOpt.isPresent()) {
                    orderIdSeq = orderOpt.get().getOrderId() + 1;
                } else {
                    orderIdSeq = 1;
                }
                System.out.println("ORDER ID = " + orderIdSeq);
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ShopServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            orderIdSeq++;
        }
        return orderIdSeq;
    }
    
    protected Database getDatabase() {
        if(database == null) {
            Database.Driver.ORACLE.load();
            database = new SQLDatabase();
            database.registerClass(
                Item.class,
                Order.class,
                OrderItem.class,
                User.class,
                Reservation.class,
                OrderReservation.class
            );
            database.connect("jdbc:oracle:thin:@178.32.41.4:8080:xe", "dbshop", "bleh");
        }
        return database;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {        
        Object logged = request.getSession().getAttribute("logged");
        if(logged == null) {
            response.sendRedirect("index.html");
            return;
        }
        
        String type = request.getParameter("type");
        
        switch(type){
            case "reservation":
                response.sendRedirect("JSPParcReservation.jsp");
                break;
            case "shop":
                response.sendRedirect("JSPShop.jsp");
                break;
            case "cart":
                response.sendRedirect("JSPPay.jsp");
                break;
            case "logout":
                request.getSession().invalidate();
                response.sendRedirect("index.html");
                break;
            case "sale":
                sale(request, response);
                response.sendRedirect("JSPShop.jsp");
                break;
            case "ticket":
                ticket(request, response);
                response.sendRedirect("JSPParcReservation.jsp");
                break;
            case "pay":
                pay(request, response);
                response.sendRedirect("JSPPay.jsp");
                break;
            case "language":
                request.getSession().setAttribute("language", request.getParameter("locale"));
                response.sendRedirect("JSPInit.jsp");
                break;
        }
    }
    
    private void sale(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        HashMap<Integer,Integer> cart = (HashMap<Integer,Integer>) session.getAttribute("cart");
        int itemId = Integer.parseInt(request.getParameter("itemId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        
        try {
            Item item = getDatabase().table(Item.class).byId(itemId).get().get();
            if(item.getStock() < quantity) {
                //response.sendError(999, "Item is not available anymore");
                return;
            }
            getDatabase().table(Item.class).update(new Item(item.getItemId(), item.getName(), item.getPrice(), item.getStock() - quantity));
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ShopServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(cart.containsKey(itemId)) {
            cart.put(itemId, cart.get(itemId)+quantity);
        } else {
            cart.put(itemId, quantity);
        }
        session.setAttribute("cart", cart);
    }
    
    private void ticket(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        HashMap<Date,Integer> reservationsList = (HashMap<Date,Integer>) session.getAttribute("reservations");
        int day = Integer.parseInt(request.getParameter("reservationDay"));
        int month = Integer.parseInt(request.getParameter("reservationMonth"));
        int year = Integer.parseInt(request.getParameter("reservationYear"));
        Date resDate = new Date(year, month-1, day);

        int quantity = Integer.parseInt(request.getParameter("quantity"));
        
        try {
            Optional<Reservation> reservationOpt = getDatabase().table(Reservation.class).byId(resDate).get();
            Reservation reservation;
            if(reservationOpt.isPresent()) {
                reservation = reservationOpt.get();
                if(reservation.getPlaceSold() + quantity > 20) {
                    return;
                }
                getDatabase().table(Reservation.class).update(new Reservation(reservation.getReservationDay(), reservation.getPlaceSold() + quantity));
                return;
            }
            getDatabase().table(Reservation.class).insert(new Reservation(resDate, quantity));
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ShopServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(reservationsList.containsKey(resDate)) {
            reservationsList.put(resDate, reservationsList.get(resDate)+quantity);
        } else {
            reservationsList.put(resDate, quantity);
        }
        session.setAttribute("reservations", reservationsList);
    }

    private void pay(HttpServletRequest request, HttpServletResponse response) {
        int orderId = getNextOrderId();
        HttpSession session = request.getSession();
        HashMap<Date,Integer> reservationsList = (HashMap<Date,Integer>) session.getAttribute("reservations");
        HashMap<Integer,Integer> cart = (HashMap<Integer,Integer>) session.getAttribute("cart");
        String username = (String) session.getAttribute("logged");
        
        getDatabase().table(Order.class).insert(new Order(orderId, username));
        
        cart.entrySet().stream().forEach((entry) -> {
            getDatabase().table(OrderItem.class).insert(new OrderItem(orderId, entry.getKey(), entry.getValue()));
        });
        reservationsList.entrySet().stream().forEach((entry) -> {
            getDatabase().table(OrderReservation.class).insert(new OrderReservation(orderId, entry.getKey(), entry.getValue()));
        });

        session.setAttribute("cart", new HashMap<Integer, Integer>());
        session.setAttribute("reservations", new HashMap<Date, Integer>());
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

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        return;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Map<Integer, Integer> cart = (Map) se.getSession().getAttribute("cart");
        Map<Date, Integer> reservationsList = (Map) se.getSession().getAttribute("reservations");
        System.out.println("cart = " + cart + "-----" + cart.size());
        System.out.println("reservationsList = " + reservationsList + "-----" + reservationsList.size());

        try {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
                Item item = getDatabase().table(Item.class).byId(entry.getKey().intValue()).get().get();
                getDatabase().table(Item.class).update(new Item(item.getItemId(), item.getName(), item.getPrice(), item.getStock() + entry.getValue().intValue()));
            }
            for (Map.Entry<Date, Integer> entry : reservationsList.entrySet()) {
                System.out.println(entry.getKey().toString() + " = " + entry.getValue());
                Reservation res = getDatabase().table(Reservation.class).byId(entry.getKey()).get().get();
                getDatabase().table(Reservation.class).update(new Reservation(res.getReservationDay(), res.getPlaceSold() - entry.getValue()));
            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ShopServlet.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }

}
