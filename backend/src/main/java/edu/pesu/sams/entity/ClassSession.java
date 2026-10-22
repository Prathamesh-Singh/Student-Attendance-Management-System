package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity @Table(name="class_sessions", indexes={@Index(name="idx_session_section_date", columnList="course_section_id,session_date")})
public class ClassSession {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="course_section_id", nullable=false) private CourseSection courseSection;
    @Column(name="session_date", nullable=false) private LocalDate date;
    @Column(name="start_time", nullable=false) private LocalTime startTime;
    @Column(name="end_time", nullable=false) private LocalTime endTime;
    @Enumerated(EnumType.STRING) @Column(name="session_type", nullable=false, length=16) private SessionType type;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="created_by_user_id", nullable=false) private UserAccount createdBy;
    @Column(nullable=false) private boolean submitted=false;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null)createdAt=LocalDateTime.now(ZoneId.of("Asia/Kolkata")); }
    public ClassSession() {}
    public Long getId(){return id;} public CourseSection getCourseSection(){return courseSection;} public void setCourseSection(CourseSection v){courseSection=v;}
    public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;} public LocalTime getStartTime(){return startTime;} public void setStartTime(LocalTime v){startTime=v;}
    public LocalTime getEndTime(){return endTime;} public void setEndTime(LocalTime v){endTime=v;} public SessionType getType(){return type;} public void setType(SessionType v){type=v;}
    public UserAccount getCreatedBy(){return createdBy;} public void setCreatedBy(UserAccount v){createdBy=v;} public boolean isSubmitted(){return submitted;} public void setSubmitted(boolean v){submitted=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
