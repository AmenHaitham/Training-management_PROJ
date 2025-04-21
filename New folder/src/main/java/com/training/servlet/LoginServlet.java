package com.training.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.training.model.User;
import com.training.service.UserService;
import com.training.util.PasswordUtil;

import java.io.IOException;

@WebServlet("/auth/login")
public class LoginServlet extends BaseServlet {
    private UserService userService;
    private PasswordUtil passwordUtil;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
        passwordUtil = new PasswordUtil();
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            // Show login form
            forwardToJsp(request, response, "login.jsp");
            return;
        }

        switch (action) {
            case "login":
                handleLogin(request, response);
                break;
            case "logout":
                handleLogout(request, response);
                break;
            default:
                forwardToJsp(request, response, "login.jsp");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        User user = userService.findByUsername(username);
        
        if (user != null && passwordUtil.verifyPassword(password, user.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());
            
            // Redirect based on role
            switch (user.getRole().toLowerCase()) {
                case "admin":
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                    break;
                case "trainer":
                    response.sendRedirect(request.getContextPath() + "/trainer/dashboard");
                    break;
                case "trainee":
                    response.sendRedirect(request.getContextPath() + "/trainee/dashboard");
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/login?error=Invalid role");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login?error=Invalid credentials");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect(request, response, "/login");
    }
} 