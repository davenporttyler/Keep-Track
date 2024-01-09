package com.tjdb.keeptrack.model;

public class Note {
    private Long id;
    private String text;
    private Long courseId;

    public Note(String text, Long courseId) {
        this.text = text;
        this.courseId = courseId;
    }

    public Note(Long id, String text, Long courseId) {
        this.id = id;
        this.text = text;
        this.courseId = courseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
