package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="report_log")
public class ReportLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="report_type", nullable=false, length=40) private String reportType;
    @Column(name="recipient_email", length=190) private String recipientEmail;
    @Column(nullable=false, length=16) private String status;
    @Column(length=500) private String details;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    public ReportLog() {}
    public ReportLog(String reportType,String recipientEmail,String status,String details,LocalDateTime createdAt){this.reportType=reportType;this.recipientEmail=recipientEmail;this.status=status;this.details=details;this.createdAt=createdAt;}
    public Long getId(){return id;} public String getReportType(){return reportType;} public String getRecipientEmail(){return recipientEmail;} public String getStatus(){return status;} public String getDetails(){return details;} public LocalDateTime getCreatedAt(){return createdAt;}
}
