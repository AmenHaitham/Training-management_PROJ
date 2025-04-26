package com.example.web.filter;

import com.example.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to check if user is authenticated for secure areas
 */
@WebFilter(urlPatterns = {"/admin/*", "/trainer/*", "/trainee/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Get the requested URL
        String requestURI = httpRequest.getRequestURI();
        
        // Get session, don't create if it doesn't exist
        HttpSession session = httpRequest.getSession(false);
        
        // Check if user is logged in
        boolean isLoggedIn = (session != null && session.getAttribute("currentUser") != null);
        
        if (isLoggedIn) {
            // Get user from session
            User user = (User) session.getAttribute("currentUser");
            String role = user.getRole();
            
            // Check role-based access
            if (requestURI.startsWith(httpRequest.getContextPath() + "/admin/") && !"ADMIN".equals(role)) {
                // User is not an admin but trying to access admin area
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/access-denied");
                return;
            } else if (requestURI.startsWith(httpRequest.getContextPath() + "/trainer/") && 
                    !("TRAINER".equals(role) || "ADMIN".equals(role))) {
                // User is not a trainer or admin but trying to access trainer area
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/access-denied");
                return;
            } else if (requestURI.startsWith(httpRequest.getContextPath() + "/trainee/") && 
                    !("TRAINEE".equals(role) || "ADMIN".equals(role))) {
                // User is not a trainee or admin but trying to access trainee area
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/access-denied");
                return;
            }
            
            // User is authenticated and has proper role, continue with the request
            chain.doFilter(request, response);
        } else {
            // User is not logged in, redirect to login page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
} 