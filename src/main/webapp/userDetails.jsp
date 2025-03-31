<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession sessionObj = request.getSession(false);
    if (sessionObj == null || sessionObj.getAttribute("email") == null) {
        response.sendRedirect("login.jsp"); // Redirect to login if not logged in
    } else {
%>

<!DOCTYPE html>
<html>
<head>
    <title>User Details</title>
</head>
<body>
    <h2>Welcome, <%= sessionObj.getAttribute("first_name") %> <%= sessionObj.getAttribute("last_name") %>!</h2>
    <p>Email: <%= sessionObj.getAttribute("email") %></p>
    <p>User ID: <%= sessionObj.getAttribute("id") %></p>

    <form action="logout.jsp" method="post">
        <button type="submit">Logout</button>
    </form>
</body>
</html>

<% } %>
