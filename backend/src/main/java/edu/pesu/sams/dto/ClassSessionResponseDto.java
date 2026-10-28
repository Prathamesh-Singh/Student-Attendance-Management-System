package edu.pesu.sams.dto;

import edu.pesu.sams.entity.SessionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class ClassSessionResponseDto {
    private Long id;
    private Long courseSectionId;
    private String courseCode;
    private String courseName;
    private String sectionName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionType type;
    private Long createdByUserId;
    private String createdByName;
    private boolean submitted;
    private LocalDateTime createdAt;

    public ClassSessionResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseSectionId() { return courseSectionId; }
    public void setCourseSectionId(Long courseSectionId) { this.courseSectionId = courseSectionId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public SessionType getType() { return type; }
    public void setType(SessionType type) { this.type = type; }
    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public boolean isSubmitted() { return submitted; }
    public void setSubmitted(boolean submitted) { this.submitted = submitted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}