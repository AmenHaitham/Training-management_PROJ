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
 * Servlet for saving user data (create/update)
 */
@WebServlet("/admin/users/save")
public class UserSaveServlet extends HttpServlet {
    
    private final UserService userService = new UserServiceImpl();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String role = request.getParameter("role");
        
        // Validate required fields
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "All fields are required");
            
            if (idParam != null && !idParam.trim().isEmpty()) {
                // Edit mode - set user and form mode
                try {
                    Long id = Long.parseLong(idParam);
                    Optional<User> user = userService.findById(id);
                    if (user.isPresent()) {
                        request.setAttribute("user", user.get());
                    }
                } catch (NumberFormatException e) {
                    // Invalid ID
                }
                request.setAttribute("formMode", "edit");
            } else {
                // Create mode
                request.setAttribute("formMode", "create");
            }
            
            // Forward back to form
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
            return;
        }
        
        try {
            if (idParam != null && !idParam.trim().isEmpty()) {
                // Edit existing user
                Long id = Long.parseLong(idParam);
                Optional<User> existingUser = userService.findById(id);
                
                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setRole(role);
                    
                    // Check if password reset is requested
                    String resetPassword = request.getParameter("resetPassword");
                    if (resetPassword != null && resetPassword.equals("on")) {
                        String newPassword = request.getParameter("newPassword");
                        if (newPassword != null && !newPassword.trim().isEmpty()) {
                            // Assuming there's a utility class for password hashing
                            // Update the user's password field with the hashed password
                            // We'll need to modify this to match the available methods
                            user.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw(newPassword, org.mindrot.jbcrypt.BCrypt.gensalt()));
                        }
                    }
                    
                    // Update user
                    userService.update(user);
                    
                    // Redirect to user list with success message
                    response.sendRedirect(request.getContextPath() + "/admin/users?message=User updated successfully");
                    return;
                } else {
                    // User not found
                    request.setAttribute("errorMessage", "User not found with ID: " + id);
                    request.setAttribute("formMode", "create");
                    request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
                    return;
                }
            } else {
                // Create new user
                String password = request.getParameter("password");
                if (password == null || password.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Password is required");
                    request.setAttribute("formMode", "create");
                    request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
                    return;
                }
                
                // Check if username already exists
                if (userService.findByUsername(username).isPresent()) {
                    request.setAttribute("errorMessage", "Username already exists");
                    request.setAttribute("formMode", "create");
                    request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
                    return;
                }
                
                // Create new user
                User newUser = new User();
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setEmail(email);
                newUser.setUsername(username);
                newUser.setRole(role);
                
                // Hash the password before setting it
                newUser.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt()));
                
                // Create new user - using the update method, assuming it can handle new entities too
                // or alternatively, the UserService might have a create method that we can use
                userService.update(newUser);
                
                // Redirect to user list with success message
                response.sendRedirect(request.getContextPath() + "/admin/users?message=User created successfully");
                return;
            }
        } catch (Exception e) {
            // Handle any exceptions
            request.setAttribute("errorMessage", "Error saving user: " + e.getMessage());
            if (idParam != null && !idParam.trim().isEmpty()) {
                request.setAttribute("formMode", "edit");
            } else {
                request.setAttribute("formMode", "create");
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
        }
    }
} 