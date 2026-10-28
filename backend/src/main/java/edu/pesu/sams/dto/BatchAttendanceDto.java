package edu.pesu.sams.dto;

import edu.pesu.sams.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BatchAttendanceDto {
    @NotNull private Long sessionId;
    @NotNull private List<StudentAttendanceDto> records;

    public BatchAttendanceDto() {}

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public List<StudentAttendanceDto> getRecords() { return records; }
    public void setRecords(List<StudentAttendanceDto> records) { this.records = records; }

    public static class StudentAttendanceDto {
        @NotNull private Long studentId;
        @NotNull private AttendanceStatus status;

        public StudentAttendanceDto() {}

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
    }
}