<%@ page session="true" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<%
    if (session.getAttribute("email") != null) {
        response.sendRedirect("userDetails.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="login-container">
        <h2>Login</h2>
        <form action="get" method="post">
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
    </div>
</body>
</html>
