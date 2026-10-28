package edu.pesu.sams.dto;

import edu.pesu.sams.entity.SessionType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClassSessionDto {
    @NotNull private Long courseSectionId;
    @NotNull private LocalDate date;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    @NotNull private SessionType type;

    public ClassSessionDto() {}

    public Long getCourseSectionId() { return courseSectionId; }
    public void setCourseSectionId(Long courseSectionId) { this.courseSectionId = courseSectionId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public SessionType getType() { return type; }
    public void setType(SessionType type) { this.type = type; }
}