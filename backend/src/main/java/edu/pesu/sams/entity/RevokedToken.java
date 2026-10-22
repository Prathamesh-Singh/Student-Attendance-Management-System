package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="revoked_tokens", uniqueConstraints=@UniqueConstraint(name="uk_revoked_jti", columnNames="jti"))
public class RevokedToken {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=64) private String jti;
    @Column(name="expires_at", nullable=false) private LocalDateTime expiresAt;
    public RevokedToken() {}
    public RevokedToken(String jti,LocalDateTime expiresAt){this.jti=jti;this.expiresAt=expiresAt;}
    public Long getId(){return id;} public String getJti(){return jti;} public LocalDateTime getExpiresAt(){return expiresAt;}
}
