package edu.pesu.sams.repository;
import edu.pesu.sams.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
public interface RevokedTokenRepository extends JpaRepository<RevokedToken,Long> {
 boolean existsByJti(String jti);
 long deleteByExpiresAtBefore(LocalDateTime now);
}
