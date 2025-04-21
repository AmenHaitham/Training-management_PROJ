package com.training.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.training.model.User;

public abstract class BaseServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    protected void forwardToJsp(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + jspPath).forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) 
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    protected boolean isUserAuthenticated(HttpServletRequest request) {
        return request.getSession().getAttribute("user") != null;
    }

    protected boolean isUserAuthorized(HttpServletRequest request, String requiredRole) {
        if (!isUserAuthenticated(request)) {
            return false;
        }
        User user = (User) request.getSession().getAttribute("user");
        return user.getRole().equals(requiredRole);
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response, 
                             String errorMessage, String jspPath)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        forwardToJsp(request, response, jspPath);
    }
} 