package com.example.model;

import java.time.LocalDateTime;

/**
 * Represents a training material in the Training Management System.
 */
public class Material {
    private Long id;
    private Long sessionId;
    private String title;
    private String description;
    private String fileName;
    private String contentType;
    private byte[] fileContent;
    private LocalDateTime uploadedAt;
    private Long uploadedBy;
    
    // Constructors
    public Material() {
    }
    
    public Material(Long id, Long sessionId, String title, String description, String fileName, 
                    String contentType, byte[] fileContent, LocalDateTime uploadedAt, Long uploadedBy) {
        this.id = id;
        this.sessionId = sessionId;
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileContent = fileContent;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public byte[] getFileContent() {
        return fileContent;
    }
    
    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public Long getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
} 