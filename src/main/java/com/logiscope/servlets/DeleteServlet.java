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

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE email=? AND password=?")) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            int deleted = stmt.executeUpdate();

            if (deleted > 0) {
                HttpSession session = request.getSession(false);
                if (session != null && email.equals(session.getAttribute("email"))) {
                    session.invalidate();
                }
                showPopup(out, "User Deleted Successfully", "index.jsp");
            } else {
                showPopup(out, "Deletion Failed! Incorrect credentials or user does not exist.", "delete.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showPopup(out, "Internal Server Error! Please try again.", "delete.jsp");
        }
    }

    private void showPopup(PrintWriter out, String message, String redirectPage) {
        out.println("<script type='text/javascript'>");
        out.println("alert('" + message + "');");
        out.println("window.location.href = '" + redirectPage + "';");
        out.println("</script>");
    }
}
