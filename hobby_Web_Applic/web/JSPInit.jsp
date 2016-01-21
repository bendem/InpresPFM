<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.sql.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@taglib uri="/WEB-INF/tlds/hobby_tag_library.tld" prefix="htl" %>
<%! ResourceBundle res; 
    String loc[];
%>
<%
    Object username = request.getParameter("username");
    String language = (String) session.getAttribute("language");
    if(session.getAttribute("logged") == null && username == null) {
        response.sendRedirect("index.html");
        return;
    } else {
        Object cart = session.getAttribute("cart");
        if(cart == null) {
            session.setAttribute("cart", new HashMap<Integer, Integer>());
            session.setAttribute("reservations", new HashMap<Date, Integer>());
            session.setAttribute("logged", username);
        }
    }
    
    if(language == null) {
        response.sendRedirect("JSPLanguage.jsp");
        return;
    }
    
    
    loc = language.split("_");
    
    res = ResourceBundle.getBundle("bundles.HobbyStrings", new Locale(loc[0], loc[1]));
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSPInit</title>
    </head>
    <body>
        <h1>JSPInit</h1>
        <htl:DateTimeTag language="<%= language %>" />
        <form method="post" action="ShopServlet">
            <input type="hidden" name="type" value="reservation">
            <input type="submit" value="<% out.write(res.getString("buttonReservation")); %>">
        </form>
        <form method="post" action="ShopServlet">
            <input type="hidden" name="type" value="shop">
            <input type="submit" value="<% out.write(res.getString("buttonShop")); %>">
        </form>
        <form method="post" action="ShopServlet">
            <input type="hidden" name="type" value="cart">
            <input type="submit" value="<% out.write(res.getString("buttonCart")); %>">
        </form>
        <form method="post" action="ShopServlet">
            <input type="hidden" name="type" value="logout">
            <input type="submit" value="<% out.write(res.getString("buttonLogout")); %>">
        </form>
    </body>
</html>
