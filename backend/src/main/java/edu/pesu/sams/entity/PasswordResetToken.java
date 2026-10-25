package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity @Table(name="password_reset_tokens", indexes=@Index(name="idx_reset_token_hash", columnList="token_hash", unique=true))
public class PasswordResetToken {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id", nullable=false) private UserAccount user;
    @Column(name="token_hash", nullable=false, length=64, unique=true) private String tokenHash;
    @Column(name="expires_at", nullable=false) private LocalDateTime expiresAt;
    @Column(name="used_at") private LocalDateTime usedAt;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){if(createdAt==null)createdAt=LocalDateTime.now(ZoneId.of("Asia/Kolkata"));}
    public PasswordResetToken() {}
    public PasswordResetToken(UserAccount user,String tokenHash,LocalDateTime expiresAt){this.user=user;this.tokenHash=tokenHash;this.expiresAt=expiresAt;}
    public Long getId(){return id;} public UserAccount getUser(){return user;} public String getTokenHash(){return tokenHash;} public LocalDateTime getExpiresAt(){return expiresAt;} public LocalDateTime getUsedAt(){return usedAt;} public void setUsedAt(LocalDateTime v){usedAt=v;}
}
