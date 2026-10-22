package edu.pesu.sams.repository;
import edu.pesu.sams.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
 List<Enrollment> findByCourseSection_IdOrderByStudent_FullNameAsc(Long sectionId);
 List<Enrollment> findByStudent_Id(Long studentId);
 boolean existsByCourseSection_IdAndStudent_Id(Long sectionId, Long studentId);
 long countByCourseSection_Id(Long sectionId);
}
