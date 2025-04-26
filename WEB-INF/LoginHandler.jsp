<%@ page import="com.example.User" %>
<%@ page import="java.sql.*" %>
<%@ page session="true" %>

<%
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    User user = new User();

    try {
        if (user.login(email, password)) {
            session.setAttribute("user", user); // store user in session
            response.sendRedirect("Home.jsp");  // redirect after successful login
        } else {
            out.println("<script>alert('Invalid email or password!'); window.location='login.jsp';</script>");
        }
    } catch (Exception e) {
        out.println("Error: " + e.getMessage());
        e.printStackTrace();
    }
%>
