package com.example.newproject;

public class StudentAssignment {
    String userId,fileName,downloadUrl;

    public StudentAssignment() {

    }

    public StudentAssignment(String userId, String fileName, String downloadUrl) {
        this.userId = userId;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
