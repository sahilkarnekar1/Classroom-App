package com.example.newproject;

public class ClassInfo {

    String classId, className, section, subject, room, userId;

    public ClassInfo() {

    }

    public ClassInfo(String classId, String className, String section, String subject, String room, String userId) {
        this.classId = classId;
        this.className = className;
        this.section = section;
        this.subject = subject;
        this.room = room;
        this.userId = userId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}