package edu.pesu.sams.repository;
import edu.pesu.sams.entity.ClassSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface ClassSessionRepository extends JpaRepository<ClassSession,Long> {
 List<ClassSession> findByCourseSection_IdAndDateBetweenOrderByDateAscStartTimeAsc(Long sectionId, LocalDate from, LocalDate to);
 List<ClassSession> findByCourseSection_IdOrderByDateDescStartTimeDesc(Long sectionId);
 List<ClassSession> findByDateOrderByStartTimeAsc(LocalDate date);
 Page<ClassSession> findByCreatedBy_Id(Long facultyId, Pageable pageable);
}
