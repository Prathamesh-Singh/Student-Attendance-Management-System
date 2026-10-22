package edu.pesu.sams.repository;
import edu.pesu.sams.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
 Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
