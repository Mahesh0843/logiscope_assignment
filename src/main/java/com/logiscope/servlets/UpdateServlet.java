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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/update")
public class UpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String newFirstName = request.getParameter("newFirstName");
        String newLastName = request.getParameter("newLastName");
        String password = request.getParameter("password");

        if (email == null || newFirstName == null || newLastName == null || password == null) {
            showPopup(out, "Invalid Request! Please provide all fields.", "update.jsp");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET first_name=?, last_name=?, updated_at=NOW() WHERE email=? AND password=?")) {
                stmt.setString(1, newFirstName);
                stmt.setString(2, newLastName);
                stmt.setString(3, email);
                stmt.setString(4, password);
                int updated = stmt.executeUpdate();

                if (updated > 0) {
                    try (PreparedStatement selectStmt = conn.prepareStatement(
                            "SELECT id, first_name, last_name, email FROM users WHERE email=?")) {
                        selectStmt.setString(1, email);
                        try (ResultSet rs = selectStmt.executeQuery()) {
                            if (rs.next()) {
                                if (session == null) {
                                    session = request.getSession(true);
                                }
                                session.setAttribute("id", rs.getInt("id"));
                                session.setAttribute("first_name", rs.getString("first_name"));
                                session.setAttribute("last_name", rs.getString("last_name"));
                                session.setAttribute("email", rs.getString("email"));

                                showPopup(out, "User details updated successfully!", "userDetails.jsp");
                                return;
                            }
                        }
                    }
                } else {
                    showPopup(out, "Update Failed! User not found.", "update.jsp");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showPopup(out, "Internal Server Error! Please try again.", "update.jsp");
        }
    }

    private void showPopup(PrintWriter out, String message, String redirectPage) {
        out.println("<script type='text/javascript'>");
        out.println("alert('" + message + "');");
        out.println("window.location.href = '" + redirectPage + "';");
        out.println("</script>");
    }
}
