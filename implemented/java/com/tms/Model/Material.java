package com.tms.Model;

import java.sql.Timestamp;

public class Material {
    private int id;
    private String title;
    private String description;
    private Session session;
    private byte[] fileData;
    private long fileSize;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public Material() {}

    public Material(String title, String description, Session session, byte[] fileData) {
        this.title = title;
        this.description = description;
        this.session = session;
        this.fileData = fileData;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
