package com.example.notesspace;

public class firebaseModel {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public firebaseModel() {
    }

    public firebaseModel(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
