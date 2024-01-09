package com.tjdb.keeptrack.model;

public class Assessment {
    private Long id;
    private String title;
    private String startDate;
    private String endDate;
    private String objective;
    private String performance;
    private Long courseId;

    public Assessment(String title, String startDate, String endDate, String objective, String performance, Long courseId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.objective = objective;
        this.performance = performance;
        this.courseId = courseId;
    }

    public Assessment(Long id, String title, String startDate, String endDate, String objective, String performance, Long courseId) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.objective = objective;
        this.performance = performance;
        this.courseId = courseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
