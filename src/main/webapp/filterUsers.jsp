<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Filter Users by Date</title>
</head>
<body>
    <h2>Filter Users by Date Range</h2>
    <form action="filterUsers" method="post">
        <label for="startDate">Start Date:</label>
        <input type="date" id="startDate" name="startDate" required>
        
        <label for="endDate">End Date:</label>
        <input type="date" id="endDate" name="endDate" required>

        <button type="submit">Get Users</button>
    </form>
</body>
</html>
