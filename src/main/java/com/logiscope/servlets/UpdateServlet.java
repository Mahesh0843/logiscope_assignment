package com.logiscope.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/update")
public class UpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        String email = request.getParameter("email");
        String newFirstName = request.getParameter("newFirstName");
        String newLastName = request.getParameter("newLastName");
        String password = request.getParameter("password");
        
        if (email == null || newFirstName == null || newLastName == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"code\": 400, \"message\": \"Invalid request! All fields are required.\"}");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET first_name=?, last_name=?, updated_at=NOW() WHERE email=? AND password=?")) {

            stmt.setString(1, newFirstName);
            stmt.setString(2, newLastName);
            stmt.setString(3, email);
            stmt.setString(4, password);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                if (session != null && email.equals(session.getAttribute("email"))) {
                    session.setAttribute("first_name", newFirstName);
                    session.setAttribute("last_name", newLastName);
                }

                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"code\": 200, \"message\": \"User details updated successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"code\": 401, \"message\": \"Update failed! Incorrect password or user not found.\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"code\": 500, \"message\": \"Internal server error! Please try again.\"}");
        }
    }
}
