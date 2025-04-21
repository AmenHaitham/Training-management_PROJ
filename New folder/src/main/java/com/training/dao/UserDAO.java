package com.training.dao;

import com.training.model.User;
import java.util.List;

public interface UserDAO {
    User create(User user);
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll();
    List<User> findByRole(String role);
    User update(User user);
    void delete(Long id);
    boolean authenticate(String username, String password);
} 