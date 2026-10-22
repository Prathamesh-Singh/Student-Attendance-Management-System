package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity @Table(name="attendance_records", uniqueConstraints=@UniqueConstraint(name="uk_attendance_session_student", columnNames={"session_id","student_user_id"}), indexes=@Index(name="idx_attendance_student", columnList="student_user_id"))
public class AttendanceRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="session_id", nullable=false) private ClassSession session;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="student_user_id", nullable=false) private UserAccount student;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private AttendanceStatus status;
    @Column(name="marked_at", nullable=false) private LocalDateTime markedAt;
    @PrePersist void prePersist(){ if(markedAt==null)markedAt=LocalDateTime.now(ZoneId.of("Asia/Kolkata")); }
    public AttendanceRecord() {}
    public AttendanceRecord(ClassSession session, UserAccount student, AttendanceStatus status){this.session=session;this.student=student;this.status=status;}
    public Long getId(){return id;} public ClassSession getSession(){return session;} public UserAccount getStudent(){return student;} public AttendanceStatus getStatus(){return status;} public void setStatus(AttendanceStatus v){status=v;} public LocalDateTime getMarkedAt(){return markedAt;}
}
