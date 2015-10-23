<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
Object resId = session.getAttribute("reservationid");
if(resId == null) {
    response.sendRedirect("ServletLog");
    return;
}
%>
<html>
<head>
    <title>Reservation information</title>
</head>
<body>
    <p>Reservation id : <%= resId %> </p>
    <p>Reservation date : <%= session.getAttribute("datereservation") %> </p>
    <p>
        Place reserved : <%= session.getAttribute("positionx") %> -
        <%= session.getAttribute("positiony") %>
    </p>
    <p>Destination : <%= session.getAttribute("destination") %> </p>
</body>
</html>
