package com.logiscope.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/create")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (firstName == null || lastName == null || email == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{"
                    + "\"code\": 400,"
                    + "\"message\": \"All fields are required\","
                    + "\"data\": null"
                    + "}");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
                out.print("{"
                        + "\"code\": 500,"
                        + "\"message\": \"Database Connection Failed\","
                        + "\"data\": null"
                        + "}");
                return;
            }

            if (userExists(email, conn)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT); 
                out.print("{"
                        + "\"code\": 409,"
                        + "\"message\": \"User Already Exists\","
                        + "\"data\": null"
                        + "}");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users (first_name, last_name, email, password, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())")) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, password);
                stmt.executeUpdate();
            }

            try (PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT id, first_name, last_name FROM users WHERE email = ?")) {
                selectStmt.setString(1, email);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        HttpSession session = request.getSession(true);
                        session.setAttribute("id", rs.getInt("id"));
                        session.setAttribute("first_name", rs.getString("first_name"));
                        session.setAttribute("last_name", rs.getString("last_name"));
                        session.setAttribute("email", email);
                        session.setMaxInactiveInterval(24 * 60 * 60);

                        response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                        out.print("{"
                                + "\"code\": 201,"
                                + "\"message\": \"Registration Successful\","
                                + "\"data\": {"
                                + "\"id\": " + rs.getInt("id") + ","
                                + "\"fnname\": \"" + rs.getString("first_name") + "\","
                                + "\"lnname\": \"" + rs.getString("last_name") + "\","
                                + "\"email\": \"" + email + "\","
                                + "\"password\": \"" + password + "\"" 
                                + "}"
                                + "}");
                        return;
                    }
                }
            }

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{"
                    + "\"code\": 500,"
                    + "\"message\": \"Registration completed, but user data retrieval failed\","
                    + "\"data\": null"
                    + "}");

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{"
                    + "\"code\": 500,"
                    + "\"message\": \"Database Error\","
                    + "\"data\": null"
                    + "}");
        }
    }

    private boolean userExists(String email, Connection connection) {
        String query = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet resultSet = ps.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
