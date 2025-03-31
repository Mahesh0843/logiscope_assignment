<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.logiscope.servlets.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Filtered Users</title>
</head>
<body>
    <h2>Filtered Users List</h2>

    <table border="1">
        <tr>
            <th>User ID</th>
            <th>First Name</th>
            <th>Last Name</th>
        </tr>

        <%
            List<User> users = (List<User>) request.getAttribute("users");
            if (users != null && !users.isEmpty()) {
                for (User user : users) {
        %>
        <tr>
            <td><%= user.getId() %></td>
            <td><%= user.getFirstName() %></td>
            <td><%= user.getLastName() %></td>
        </tr>
        <%
                }
            } else { 
        %>
        <tr>
            <td colspan="3">No users found in this date range.</td>
        </tr>
        <% } %>
    </table>

    <br>
    <a href="filterUsers.jsp">Back to Date Filter</a>
</body>
</html>
