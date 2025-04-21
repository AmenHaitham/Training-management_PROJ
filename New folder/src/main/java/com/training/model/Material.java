package com.training.model;

import java.util.Date;

public class Material {
    private Long id;
    private Session session;
    private String title;
    private String description;
    private String filePath;
    private String fileType;
    private User uploadedBy;
    private Date uploadedAt;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Material() {}

    public Material(Session session, String title, String description, String filePath, 
                   String fileType, User uploadedBy) {
        this.session = session;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = new Date();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }

    public Date getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Date uploadedAt) { this.uploadedAt = uploadedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // Business Methods
    public String getFileExtension() {
        int lastDotIndex = filePath.lastIndexOf('.');
        return lastDotIndex > 0 ? filePath.substring(lastDotIndex + 1) : "";
    }

    public String getFileName() {
        int lastSlashIndex = filePath.lastIndexOf('/');
        return lastSlashIndex > 0 ? filePath.substring(lastSlashIndex + 1) : filePath;
    }

    public boolean isImage() {
        String ext = getFileExtension().toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif");
    }

    public boolean isPDF() {
        return getFileExtension().toLowerCase().equals("pdf");
    }

    public boolean isVideo() {
        String ext = getFileExtension().toLowerCase();
        return ext.equals("mp4") || ext.equals("avi") || ext.equals("mov") || ext.equals("wmv");
    }

    public String getFileSize() {
        // This would be implemented to return the actual file size
        return "0 KB";
    }
} 