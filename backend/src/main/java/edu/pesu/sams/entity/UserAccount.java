package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity @Table(name="users", uniqueConstraints={@UniqueConstraint(name="uk_users_institution_id", columnNames="institution_id"), @UniqueConstraint(name="uk_users_email", columnNames="email")})
public class UserAccount {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="institution_id", nullable=false, length=64) private String institutionId;
    @Column(name="full_name", nullable=false, length=160) private String fullName;
    @Column(nullable=false, length=190) private String email;
    @Column(name="password_hash", nullable=false, length=100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private UserRole role;
    @Column(nullable=false) private boolean active=true;
    @Column(name="failed_login_attempts", nullable=false) private int failedLoginAttempts=0;
    @Column(name="locked_until") private LocalDateTime lockedUntil;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @Column(name="updated_at", nullable=false) private LocalDateTime updatedAt;

    @PrePersist void onCreate(){ LocalDateTime n=LocalDateTime.now(ZoneId.of("Asia/Kolkata")); if(createdAt==null)createdAt=n; if(updatedAt==null)updatedAt=n; }
    @PreUpdate void onUpdate(){ updatedAt=LocalDateTime.now(ZoneId.of("Asia/Kolkata")); }
    public UserAccount() {}
    public Long getId(){return id;} public String getInstitutionId(){return institutionId;} public void setInstitutionId(String v){institutionId=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public UserRole getRole(){return role;} public void setRole(UserRole v){role=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    public int getFailedLoginAttempts(){return failedLoginAttempts;} public void setFailedLoginAttempts(int v){failedLoginAttempts=v;}
    public LocalDateTime getLockedUntil(){return lockedUntil;} public void setLockedUntil(LocalDateTime v){lockedUntil=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
