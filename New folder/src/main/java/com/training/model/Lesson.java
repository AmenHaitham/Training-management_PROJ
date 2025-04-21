package com.training.model;

import java.util.Date;

public class Lesson {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private int duration; // in minutes
    private Module module;
    private int orderNumber;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Lesson() {}

    public Lesson(Long id, String title, String content, String videoUrl, int duration, 
                 Module module, int orderNumber) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.module = module;
        this.orderNumber = orderNumber;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    
    public int getOrderNumber() { return orderNumber; }
    public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
} 