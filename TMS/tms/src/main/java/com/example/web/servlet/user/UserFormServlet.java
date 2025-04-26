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
import java.util.Optional;

/**
 * Servlet for user form (add/edit)
 */
@WebServlet("/admin/users/form")
public class UserFormServlet extends HttpServlet {
    
    private final UserService userService = new UserServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        // Check if this is an edit (id provided) or create (no id)
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(idParam);
                Optional<User> user = userService.findById(id);
                
                if (user.isPresent()) {
                    request.setAttribute("user", user.get());
                    request.setAttribute("formMode", "edit");
                } else {
                    // User not found
                    request.setAttribute("errorMessage", "User not found with ID: " + id);
                    request.setAttribute("formMode", "create");
                }
            } catch (NumberFormatException e) {
                // Invalid ID
                request.setAttribute("errorMessage", "Invalid user ID: " + idParam);
                request.setAttribute("formMode", "create");
            }
        } else {
            // Create mode
            request.setAttribute("formMode", "create");
        }
        
        // Forward to the user form JSP
        request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
    }
} 