<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Reservation information</title>
</head>
<body>
<%
    Object resId = session.getAttribute("reservationid");
    if(resId != null) {
%>
    <p>Reservation id : <%= resId %> </p>
    <p>Reservation date : <%= session.getAttribute("datereservation") %> </p>
    <p>Place reserved : <%= session.getAttribute("positionx") %> - <%= session.getAttribute("positiony") %> </p>
    <p>Destination : <%= session.getAttribute("destination") %> </p>
<%
        session.removeAttribute("reservationid");
    } else {
        Object logged = session.getAttribute("logged");
        if(logged != null) {
            response.sendRedirect("ServletLog");
        } else {
            response.sendRedirect("login.html");
        }
    }
%>
</body>
</html>
