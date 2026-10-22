package edu.pesu.sams.repository;
import edu.pesu.sams.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord,Long> {
 List<AttendanceRecord> findBySession_Id(Long sessionId);
 List<AttendanceRecord> findByStudent_Id(Long studentId);
 Optional<AttendanceRecord> findBySession_IdAndStudent_Id(Long sessionId,Long studentId);
 long countByStudent_IdAndSession_CourseSection_Id(Long studentId,Long sectionId);
 long countByStudent_IdAndSession_CourseSection_IdAndStatusIn(Long studentId,Long sectionId,Collection<edu.pesu.sams.entity.AttendanceStatus> statuses);
}
