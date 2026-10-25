package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity @Table(name="audit_log", indexes={@Index(name="idx_audit_created", columnList="created_at"), @Index(name="idx_audit_actor", columnList="actor_email"), @Index(name="idx_audit_action", columnList="action")})
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="actor_email", length=190) private String actorEmail;
    @Column(nullable=false, length=80) private String action;
    @Column(name="target_type", length=80) private String targetType;
    @Column(name="target_id", length=100) private String targetId;
    @Column(name="old_value", columnDefinition="TEXT") private String oldValue;
    @Column(name="new_value", columnDefinition="TEXT") private String newValue;
    @Column(length=250) private String reason;
    @Column(name="source_ip", length=64) private String sourceIp;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){if(createdAt==null)createdAt=LocalDateTime.now(ZoneId.of("Asia/Kolkata"));}
    public AuditLog() {}
    public AuditLog(String actorEmail,String action,String targetType,String targetId,String oldValue,String newValue,String reason,String sourceIp){this.actorEmail=actorEmail;this.action=action;this.targetType=targetType;this.targetId=targetId;this.oldValue=oldValue;this.newValue=newValue;this.reason=reason;this.sourceIp=sourceIp;}
    public Long getId(){return id;} public String getActorEmail(){return actorEmail;} public String getAction(){return action;} public String getTargetType(){return targetType;} public String getTargetId(){return targetId;} public String getOldValue(){return oldValue;} public String getNewValue(){return newValue;} public String getReason(){return reason;} public String getSourceIp(){return sourceIp;} public LocalDateTime getCreatedAt(){return createdAt;}
}
