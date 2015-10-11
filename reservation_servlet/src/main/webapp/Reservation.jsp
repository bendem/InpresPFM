<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Reservation information</title>
</head>
<body>
<%
    Object resId = session.getAttribute("reservationid");
    if(resId != null){
%>
    <p>Reservation id : </p> <%=resId%>
    <p>Reservation date : </p> <%=session.getAttribute("datereservation")%>
    <p>Place reserved : </p> <%=session.getAttribute("positionx")%> <p>-</p> <%=session.getAttribute("positiony")%>
    <p>Destination : </p> <%=session.getAttribute("destination")%>
<%
    } else {
        response.sendRedirect("login.html");
    }
%>
</body>
</html>
