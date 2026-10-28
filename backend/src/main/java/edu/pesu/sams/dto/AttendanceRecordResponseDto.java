package edu.pesu.sams.dto;

import edu.pesu.sams.entity.AttendanceStatus;
import java.time.LocalDateTime;

public class AttendanceRecordResponseDto {
    private Long id;
    private Long sessionId;
    private LocalDateTime sessionDate;
    private LocalTime sessionStartTime;
    private LocalTime sessionEndTime;
    private String courseCode;
    private String courseName;
    private String sectionName;
    private Long studentId;
    private String studentInstitutionId;
    private String studentName;
    private AttendanceStatus status;
    private LocalDateTime markedAt;

    public AttendanceRecordResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public LocalDateTime getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDateTime sessionDate) { this.sessionDate = sessionDate; }
    public LocalTime getSessionStartTime() { return sessionStartTime; }
    public void setSessionStartTime(LocalTime sessionStartTime) { this.sessionStartTime = sessionStartTime; }
    public LocalTime getSessionEndTime() { return sessionEndTime; }
    public void setSessionEndTime(LocalTime sessionEndTime) { this.sessionEndTime = sessionEndTime; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentInstitutionId() { return studentInstitutionId; }
    public void setStudentInstitutionId(String studentInstitutionId) { this.studentInstitutionId = studentInstitutionId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }
}