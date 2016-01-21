<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/hobby_tag_library.tld" prefix="htl" %>
<%  Object logged = request.getSession().getAttribute("logged");
    if(logged == null) {
        response.sendRedirect("index.html");
        return;
    }
    String language = (String) session.getAttribute("language");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Parc Reservation</title>
    </head>
    <body>
        <h1>JSPParcReservation</h1>
        <htl:DateTimeTag language="<%= language %>" />
        <form method="post" action="ShopServlet">
            <input type="hidden" name="type" value="ticket">
            Reservation date : <input type="number" name="reservationDay" value="<% out.write(Integer.toString(new Date().getDate())); %>"> 
            / <input type="number" name="reservationMonth" value="<% out.write(Integer.toString(new Date().getMonth()+1)); %>"> 
            / <input type="number" name="reservationYear" value="2016"><br>
            Quantity: <input type="number" min="0" max="20" name="quantity"><br>
            <input type="submit" value="Order"><br><br>
        </form>
    </body>
</html>
