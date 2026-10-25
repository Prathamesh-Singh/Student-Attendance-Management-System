package edu.pesu.sams.repository;
import edu.pesu.sams.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.*;
public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox,Long> {
 List<NotificationOutbox> findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(OutboxStatus status,LocalDateTime now);
 long countByStatus(OutboxStatus status);
}
