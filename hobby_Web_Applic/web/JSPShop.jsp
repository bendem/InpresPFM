<%@page import="java.util.ResourceBundle"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.List"%>
<%@page import="be.hepl.benbear.shopdb.Reservation"%>
<%@page import="be.hepl.benbear.shopdb.User"%>
<%@page import="be.hepl.benbear.shopdb.OrderItem"%>
<%@page import="be.hepl.benbear.shopdb.Order"%>
<%@page import="be.hepl.benbear.shopdb.Item"%>
<%@page import="be.hepl.benbear.commons.db.SQLDatabase"%>
<%@page import="be.hepl.benbear.commons.db.Database"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/hobby_tag_library.tld" prefix="htl" %>
<%! private Database database; 
    private List<Item> items;
    private ResourceBundle res; 
    private String loc[];%>
<%  Object logged = request.getSession().getAttribute("logged");
    if(logged == null) {
        response.sendRedirect("index.html");
        return;
    }
    
    String language = (String) session.getAttribute("language");
    
    loc = language.split("_");
    
    res = ResourceBundle.getBundle("bundles.HobbyStrings", new Locale(loc[0], loc[1]));
    
    Database.Driver.ORACLE.load();
    database = new SQLDatabase();
    database.registerClass(
        Item.class,
        Order.class,
        OrderItem.class,
        User.class,
        Reservation.class
    );
    database.connect("jdbc:oracle:thin:@178.32.41.4:8080:xe", "dbshop", "bleh");
    
    items = database.table(Item.class).find().get().collect(Collectors.<Item>toList());
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSPShop</title>
    </head>
    <body>
        <h1>JSPShop</h1>
        <htl:DateTimeTag language="<%= language %>" />
        <%  for(Item item : items){ 
                if(item.getStock() > 0) { %>
                    <form method="post" action="ShopServlet">
                        <input type="hidden" name="type" value="sale">
                        <input type="hidden" name="itemId" value="<% out.write(Integer.toString(item.getItemId())); %>">
                        Id: <% out.write(Integer.toString(item.getItemId())); %><br>
                        Name: <% out.write(item.getName()); %><br>
                        Price: <% out.write(Double.toString(item.getPrice())); %><br>
                        Stock: <% out.write(Integer.toString(item.getStock())); %><br>
                        Quantity: <input type="number" min="0" max="<% out.write(Integer.toString(item.getStock())); %>" name="quantity"><br>
                        <input type="submit" value="<%= res.getString("buttonOrder") %>"><br><br>
                    </form>
        <%      }
        }%>
    </body>
</html>
