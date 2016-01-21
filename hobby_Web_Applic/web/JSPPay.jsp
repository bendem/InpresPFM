<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/hobby_tag_library.tld" prefix="htl" %>
<%! ResourceBundle res; 
    String loc[];
%>
<%
    String language = (String) session.getAttribute("language");
    if(session.getAttribute("logged") == null) {
        response.sendRedirect("index.html");
        return;
    }    
    loc = language.split("_");
    
    res = ResourceBundle.getBundle("bundles.HobbyStrings", new Locale(loc[0], loc[1]));

    double total = 0;
    pageContext.setAttribute("totalCart", total);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSPPay</title>
    </head>
    <body>
        <h1>JSPPay</h1>
        <htl:DateTimeTag language="<%= language %>" />
        <htl:DisplayCart>cart</htl:DisplayCart>
        <htl:DisplayReservation>reservations</htl:DisplayReservation>
        <h3>Total: <%= pageContext.getAttribute("totalCart") %></h3>
        <form method="post" action="ShopServlet">
            <input type="hidden" name="type" value="pay">
            <input type="submit" value="<%= res.getString("buttonPay") %>">
        </form>
    </body>
</html>
