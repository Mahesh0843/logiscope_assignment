package com.logiscope.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;



@WebServlet("/filterUsers")
public class UserFilterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String startDate = request.getParameter("startDate") + " 00:00:00";
        String endDate = request.getParameter("endDate") + " 23:59:59";

        List<User> users = fetchUsers(startDate, endDate);
        request.setAttribute("users", users);
        request.getRequestDispatcher("displayUsers.jsp").forward(request, response);
    }

    private List<User> fetchUsers(String startDate, String endDate) {
        String sql = "SELECT id, first_name, last_name FROM users WHERE created_at BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, startDate);
            stmt.setString(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(new User(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name")));
                }
                return users;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
