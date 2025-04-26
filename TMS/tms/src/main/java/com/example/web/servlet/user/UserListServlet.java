package com.example.web.servlet.user;

import com.example.model.User;
import com.example.service.UserService;
import com.example.service.impl.UserServiceImpl;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet for listing and managing users
 */
@WebServlet("/users")
public class UserListServlet extends HttpServlet {
    private final UserService userService;
    private static final int PAGE_SIZE = 10;
    
    public UserListServlet() {
        // Assuming there's a UserServiceImpl implementation class
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get filter parameters
        String nameFilter = request.getParameter("name");
        String emailFilter = request.getParameter("email");
        String roleFilter = request.getParameter("role");
        
        // Get page parameters
        int page = 1;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                // Ignore and use default page 1
            }
        }
        
        // Get all users first (since UserService doesn't have pagination methods)
        List<User> allUsers = userService.findAll();
        
        // Apply filters if needed
        if (!isEmpty(nameFilter) || !isEmpty(emailFilter) || !isEmpty(roleFilter)) {
            allUsers = allUsers.stream()
                .filter(user -> isEmpty(nameFilter) || 
                        (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(user -> isEmpty(emailFilter) || 
                        user.getEmail().toLowerCase().contains(emailFilter.toLowerCase()))
                .filter(user -> isEmpty(roleFilter) || 
                        user.getRole().equalsIgnoreCase(roleFilter))
                .collect(Collectors.toList());
        }
        
        // Count total users for pagination
        int totalUsers = allUsers.size();
        
        // Calculate pagination limits
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalUsers);
        
        // Get the subset of users for the current page
        List<User> usersForPage = startIndex < totalUsers ? 
                allUsers.subList(startIndex, endIndex) : new ArrayList<>();
        
        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);
        
        // Set attributes for the view
        request.setAttribute("users", usersForPage);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);
        
        // Set filter attributes to maintain state
        request.setAttribute("nameFilter", nameFilter);
        request.setAttribute("emailFilter", emailFilter);
        request.setAttribute("roleFilter", roleFilter);
        
        // Forward to the view
        request.getRequestDispatcher("/WEB-INF/views/user/list.jsp").forward(request, response);
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
} 