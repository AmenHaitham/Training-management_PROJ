package com.training.dao;

import com.training.model.Course;
import java.util.List;

public interface CourseDAO {
    Course create(Course course);
    Course findById(Long id);
    List<Course> findAll();
    List<Course> findByTrainer(Long trainerId);
    List<Course> findByCategory(String category);
    List<Course> findByStatus(String status);
    Course update(Course course);
    void delete(Long id);
    List<Course> search(String keyword);
} 