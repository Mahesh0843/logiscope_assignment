<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession sessionObj = request.getSession(false);
    if (sessionObj != null) {
        sessionObj.removeAttribute("email");
        sessionObj.removeAttribute("id");
        sessionObj.removeAttribute("first_name");
        sessionObj.removeAttribute("last_name");
        sessionObj.invalidate();
    }
    response.sendRedirect("login.jsp");
%>
