package edu.pesu.sams.repository;
import edu.pesu.sams.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserAccountRepository extends JpaRepository<UserAccount,Long> {
 Optional<UserAccount> findByEmailIgnoreCase(String email);
 Optional<UserAccount> findByInstitutionIdIgnoreCase(String institutionId);
 boolean existsByEmailIgnoreCase(String email);
 boolean existsByInstitutionIdIgnoreCase(String institutionId);
 List<UserAccount> findByRoleAndActiveTrue(UserRole role);
 List<UserAccount> findByRole(UserRole role);
}
