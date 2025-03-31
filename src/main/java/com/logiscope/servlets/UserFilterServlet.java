package com.logiscope.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/filterUsers")
public class UserFilterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        // Validate input fields
        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"code\": 400, \"message\": \"Invalid request! Both startDate and endDate are required.\"}");
            return;
        }

        String formattedStartDate = startDate + " 00:00:00";
        String formattedEndDate = endDate + " 23:59:59";

        List<User> users = fetchUsers(formattedStartDate, formattedEndDate);

        // Construct JSON manually
        String usersJson = users.stream()
            .map(user -> String.format("{\"id\": %d, \"firstName\": \"%s\", \"lastName\": \"%s\"}", 
                                        user.getId(), user.getFirstName(), user.getLastName()))
            .collect(Collectors.joining(",", "[", "]"));

        if (users.isEmpty()) {
            out.println("{\"code\": 200, \"message\": \"No users found in the given date range.\", \"users\": []}");
        } else {
            out.println("{\"code\": 200, \"message\": \"Users fetched successfully.\", \"users\": " + usersJson + "}");
        }
    }

    private List<User> fetchUsers(String startDate, String endDate) {
        String sql = "SELECT id, first_name, last_name FROM users WHERE created_at BETWEEN ? AND ?";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, startDate);
            stmt.setString(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}
