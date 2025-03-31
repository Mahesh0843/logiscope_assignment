package com.logiscope.servlets;

import java.io.IOException;
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
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("email") != null) {
            response.sendRedirect("userDetails.jsp");
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, first_name, last_name FROM users WHERE email = ? AND password = ?")) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                session = request.getSession();
                session.setAttribute("email", email);
                session.setAttribute("id", rs.getInt("id"));
                session.setAttribute("first_name", rs.getString("first_name"));
                session.setAttribute("last_name", rs.getString("last_name"));
                session.setMaxInactiveInterval(24 * 60 * 60); 

                response.sendRedirect("userDetails.jsp");
            } else {
                showPopup(response, "Incorrect Email or Password", "login.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void showPopup(HttpServletResponse response, String message, String redirectPage) throws IOException {
        response.getWriter().println("<script type='text/javascript'>");
        response.getWriter().println("alert('" + message + "');");
        response.getWriter().println("window.location.href = '" + redirectPage + "';");
        response.getWriter().println("</script>");
    }
}
