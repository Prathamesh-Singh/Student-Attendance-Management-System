package edu.pesu.sams.dto;

import edu.pesu.sams.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public class AttendanceRecordDto {
    @NotNull private Long sessionId;
    @NotNull private Long studentId;
    @NotNull private AttendanceStatus status;

    public AttendanceRecordDto() {}

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
}