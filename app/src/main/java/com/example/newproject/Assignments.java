package com.example.newproject;

public class Assignments {

    String assignmentTitle, assignmentDesc, downloadUrl, fileUri, userId, assignmentId,classId;

    public Assignments() {
    }

    public Assignments(String assignmentTitle, String assignmentDesc, String downloadUrl, String fileUri, String userId, String assignmentId, String classId) {
        this.assignmentTitle = assignmentTitle;
        this.assignmentDesc = assignmentDesc;
        this.downloadUrl = downloadUrl;
        this.fileUri = fileUri;
        this.userId = userId;
        this.assignmentId = assignmentId;
        this.classId = classId;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public String getAssignmentDesc() {
        return assignmentDesc;
    }

    public void setAssignmentDesc(String assignmentDesc) {
        this.assignmentDesc = assignmentDesc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}