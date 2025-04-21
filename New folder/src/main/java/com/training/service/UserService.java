package com.training.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.training.dao.UserDAO;
import com.training.dao.impl.UserDAOImpl;
import com.training.model.User;
import com.training.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAOImpl();
    }
    
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    return user;
                }
            }
        }
        return null;
    }

    public User findByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error finding user by username: " + username, e);
            return null;
        }
    }
} 