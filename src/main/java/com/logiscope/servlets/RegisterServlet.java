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
        response.setContentType("text/html;charset=UTF-8");

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DatabaseConnection.getConnection();
             PrintWriter out = response.getWriter()) {

            if (conn == null) {
                showPopup(out, "Database Connection Failed!", "register.jsp");
                return;
            }

            if (userExists(email, conn)) {
                showPopup(out, "User Already Exists for this Email Address!", "register.jsp");
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
                        HttpSession oldSession = request.getSession(false);
                        if (oldSession != null) {
                            oldSession.invalidate();
                        }

                        // Start a new session
                        HttpSession newSession = request.getSession(true);
                        newSession.setAttribute("id", rs.getInt("id"));
                        newSession.setAttribute("first_name", rs.getString("first_name"));
                        newSession.setAttribute("last_name", rs.getString("last_name"));
                        newSession.setAttribute("email", email);
                        newSession.setMaxInactiveInterval(24 * 60 * 60); // 24 hours

                        showPopup(out, "Registration Successful!", "userDetails.jsp");
                        return;
                    }
                }
            }
            showPopup(out, "Registration Successful, but login failed. Try again.", "login.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
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

    private void showPopup(PrintWriter out, String message, String redirectPage) {
        out.println("<script type='text/javascript'>");
        out.println("alert('" + message + "');");
        out.println("window.location.href = '" + redirectPage + "';");
        out.println("</script>");
    }
}
