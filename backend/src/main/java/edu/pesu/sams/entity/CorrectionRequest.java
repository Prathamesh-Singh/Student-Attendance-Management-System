package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity @Table(name="correction_requests")
public class CorrectionRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="attendance_record_id", nullable=false) private AttendanceRecord attendanceRecord;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="student_user_id", nullable=false) private UserAccount student;
    @Column(nullable=false, length=250) private String reason;
    @Enumerated(EnumType.STRING) @Column(name="requested_status", nullable=false, length=16) private AttendanceStatus requestedStatus;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private CorrectionStatus status=CorrectionStatus.PENDING;
    @Column(name="requested_at", nullable=false) private LocalDateTime requestedAt;
    @Column(name="decided_at") private LocalDateTime decidedAt;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="decided_by_user_id") private UserAccount decidedBy;
    @Column(name="decision_note", length=250) private String decisionNote;
    @PrePersist void prePersist(){if(requestedAt==null)requestedAt=LocalDateTime.now(ZoneId.of("Asia/Kolkata"));}
    public CorrectionRequest() {}
    public CorrectionRequest(AttendanceRecord attendanceRecord, UserAccount student, AttendanceStatus requestedStatus, String reason){this.attendanceRecord=attendanceRecord;this.student=student;this.requestedStatus=requestedStatus;this.reason=reason;}
    public Long getId(){return id;} public AttendanceRecord getAttendanceRecord(){return attendanceRecord;} public UserAccount getStudent(){return student;} public String getReason(){return reason;}
    public AttendanceStatus getRequestedStatus(){return requestedStatus;} public CorrectionStatus getStatus(){return status;} public void setStatus(CorrectionStatus v){status=v;} public LocalDateTime getRequestedAt(){return requestedAt;} public LocalDateTime getDecidedAt(){return decidedAt;} public void setDecidedAt(LocalDateTime v){decidedAt=v;} public UserAccount getDecidedBy(){return decidedBy;} public void setDecidedBy(UserAccount v){decidedBy=v;} public String getDecisionNote(){return decisionNote;} public void setDecisionNote(String v){decisionNote=v;}
}
