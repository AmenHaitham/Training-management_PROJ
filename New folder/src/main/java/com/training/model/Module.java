package com.training.model;

import java.util.Date;
import java.util.List;

public class Module {
    private Long id;
    private String title;
    private String description;
    private int orderNumber;
    private Course course;
    private List<Lesson> lessons;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Module() {}

    public Module(Long id, String title, String description, int orderNumber, Course course) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.orderNumber = orderNumber;
        this.course = course;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getOrderNumber() { return orderNumber; }
    public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
} 