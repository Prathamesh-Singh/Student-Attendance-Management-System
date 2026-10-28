package edu.pesu.sams.service;

import edu.pesu.sams.dto.AttendanceRecordDto;
import edu.pesu.sams.dto.AttendanceRecordResponseDto;
import edu.pesu.sams.entity.*;
import edu.pesu.sams.exception.SamsException;
import edu.pesu.sams.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:sams;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
@Transactional
class AttendanceServiceTest {

    @Autowired UserAccountRepository users;
    @Autowired CourseSectionRepository sections;
    @Autowired EnrollmentRepository enrolments;
    @Autowired ClassSessionRepository sessions;
    @Autowired AttendanceRecordRepository records;
    @Autowired AttendanceService attendanceService;

    private UserAccount faculty(String institutionId, String email) {
        UserAccount u = new UserAccount();
        u.setInstitutionId(institutionId);
        u.setFullName(institutionId + " Name");
        u.setEmail(email);
        u.setRole(UserRole.FACULTY);
        u.setPasswordHash("$2a$12$placeholderhashplaceholderhashplaceholderhashplacehold");
        return users.saveAndFlush(u);
    }

    private UserAccount student(String institutionId, String email) {
        UserAccount u = new UserAccount();
        u.setInstitutionId(institutionId);
        u.setFullName(institutionId + " Name");
        u.setEmail(email);
        u.setRole(UserRole.STUDENT);
        u.setPasswordHash("$2a$12$placeholderhashplaceholderhashplaceholderhashplacehold");
        return users.saveAndFlush(u);
    }

    private CourseSection section(String code, UserAccount... faculty) {
        CourseSection s = new CourseSection();
        s.setCourseCode(code);
        s.setCourseName("Software Engineering");
        s.setSemester("5");
        s.setSection("A");
        s.setAcademicYear("2026-27");
        s.setFaculty(new java.util.HashSet<>(java.util.List.of(faculty)));
        return sections.saveAndFlush(s);
    }

    private Enrollment enrollment(CourseSection sec, UserAccount stu) {
        return enrolments.saveAndFlush(new Enrollment(sec, stu));
    }

    private ClassSession session(CourseSection sec, UserAccount fac) {
        ClassSession c = new ClassSession();
        c.setCourseSection(sec);
        c.setDate(LocalDate.of(2026, 10, 22));
        c.setStartTime(LocalTime.of(9, 0));
        c.setEndTime(LocalTime.of(10, 0));
        c.setType(SessionType.LECTURE);
        c.setCreatedBy(fac);
        return sessions.saveAndFlush(c);
    }

    private AttendanceRecordDto dto(Long sessionId, Long studentId, AttendanceStatus status) {
        AttendanceRecordDto d = new AttendanceRecordDto();
        d.setSessionId(sessionId);
        d.setStudentId(studentId);
        d.setStatus(status);
        return d;
    }

    @Test void facultyCanMarkAttendanceForEnrolledStudent() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        enrollment(sec, stu);
        ClassSession sess = session(sec, fac);

        AttendanceRecordResponseDto marked = attendanceService.markAttendance(
                dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), fac.getEmail());

        assertNotNull(marked.getId());
        assertEquals(AttendanceStatus.PRESENT, marked.getStatus());
        assertEquals(stu.getId(), marked.getStudentId());
    }

    @Test void cannotMarkAttendanceForUnenrolledStudent() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        ClassSession sess = session(sec, fac);

        assertThrows(SamsException.class, () -> attendanceService.markAttendance(
                dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), fac.getEmail()));
    }

    @Test void cannotMarkDuplicateAttendance() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        enrollment(sec, stu);
        ClassSession sess = session(sec, fac);

        attendanceService.markAttendance(dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), fac.getEmail());

        assertThrows(SamsException.class, () -> attendanceService.markAttendance(
                dto(sess.getId(), stu.getId(), AttendanceStatus.ABSENT), fac.getEmail()));
    }

    @Test void cannotMarkAttendanceForSubmittedSession() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        enrollment(sec, stu);
        ClassSession sess = session(sec, fac);
        sess.setSubmitted(true);
        sessions.saveAndFlush(sess);

        assertThrows(SamsException.class, () -> attendanceService.markAttendance(
                dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), fac.getEmail()));
    }

    @Test void otherFacultyCannotMarkAttendance() {
        UserAccount fac1 = faculty("FAC001", "fac1@sams.local");
        UserAccount fac2 = faculty("FAC002", "fac2@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac1);
        enrollment(sec, stu);
        ClassSession sess = session(sec, fac1);

        assertThrows(SamsException.class, () -> attendanceService.markAttendance(
                dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), fac2.getEmail()));
    }

    @Test void studentCannotMarkAttendance() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        enrollment(sec, stu);
        ClassSession sess = session(sec, fac);

        assertThrows(SamsException.class, () -> attendanceService.markAttendance(
                dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), stu.getEmail()));
    }

    @Test void facultyCanViewSessionAttendance() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        enrollment(sec, stu);
        ClassSession sess = session(sec, fac);

        attendanceService.markAttendance(dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), fac.getEmail());

        var list = attendanceService.getAttendanceBySession(sess.getId(), fac.getEmail());
        assertEquals(1, list.size());
        assertEquals(AttendanceStatus.PRESENT, list.get(0).getStatus());
    }

    @Test void enrolledStudentCanViewOwnAttendance() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        enrollment(sec, stu);
        ClassSession sess = session(sec, fac);

        attendanceService.markAttendance(dto(sess.getId(), stu.getId(), AttendanceStatus.PRESENT), fac.getEmail());

        var list = attendanceService.getAttendanceByStudent(stu.getId(), stu.getEmail());
        assertEquals(1, list.size());
    }

    @Test void studentCannotViewOtherStudentAttendance() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        UserAccount stu1 = student("STU1001", "stu1@sams.local");
        UserAccount stu2 = student("STU1002", "stu2@sams.local");
        CourseSection sec = section("UE23CS341A", fac);
        enrollment(sec, stu1);
        enrollment(sec, stu2);
        ClassSession sess = session(sec, fac);

        attendanceService.markAttendance(dto(sess.getId(), stu1.getId(), AttendanceStatus.PRESENT), fac.getEmail());

        assertThrows(SamsException.class, () -> attendanceService.getAttendanceByStudent(stu1.getId(), stu2.getEmail()));
    }
}