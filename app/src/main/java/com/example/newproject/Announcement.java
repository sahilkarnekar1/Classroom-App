package com.example.newproject;

public class Announcement {
    private String announcementText;
    private String fileUrl;
    private String fileName;
    private String userId;

    // Required empty constructor for Firebase
    public Announcement() {
    }

    public Announcement(String announcementText, String fileUrl, String fileName, String userId) {
        this.announcementText = announcementText;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.userId = userId;
    }

    public String getAnnouncementText() {
        return announcementText;
    }

    public void setAnnouncementText(String announcementText) {
        this.announcementText = announcementText;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}