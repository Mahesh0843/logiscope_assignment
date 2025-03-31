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

@WebServlet("/get")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{"
                    + "\"code\": 400,"
                    + "\"message\": \"Email and password are required\","
                    + "\"data\": null"
                    + "}");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, first_name, last_name FROM users WHERE email = ? AND password = ?")) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("id", rs.getInt("id"));
                session.setAttribute("first_name", rs.getString("first_name"));
                session.setAttribute("last_name", rs.getString("last_name"));
                session.setAttribute("email", email);
                session.setAttribute("password", password); 
                session.setMaxInactiveInterval(24 * 60 * 60);

                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{"
                        + "\"code\": 200,"
                        + "\"message\": \"success\","
                        + "\"data\": {"
                        + "\"id\": " + rs.getInt("id") + ","
                        + "\"fnname\": \"" + rs.getString("first_name") + "\","
                        + "\"Inname\": \"" + rs.getString("last_name") + "\","
                        + "\"email\": \"" + email + "\","
                        + "\"password\": \"" + password + "\""
                        + "}"
                        + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
                out.print("{"
                        + "\"code\": 401,"
                        + "\"message\": \"Invalid email or password\","
                        + "\"data\": null"
                        + "}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{"
                    + "\"code\": 500,"
                    + "\"message\": \"Database error\","
                    + "\"data\": null"
                    + "}");
        }
    }
}
