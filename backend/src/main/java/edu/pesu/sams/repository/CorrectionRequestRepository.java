package edu.pesu.sams.repository;
import edu.pesu.sams.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CorrectionRequestRepository extends JpaRepository<CorrectionRequest,Long> {
 List<CorrectionRequest> findByStudent_EmailIgnoreCaseOrderByRequestedAtDesc(String email);
 List<CorrectionRequest> findByStatusOrderByRequestedAtAsc(CorrectionStatus status);
 List<CorrectionRequest> findByStudent_EmailIgnoreCaseAndStatusOrderByRequestedAtDesc(String email,CorrectionStatus status);
}
