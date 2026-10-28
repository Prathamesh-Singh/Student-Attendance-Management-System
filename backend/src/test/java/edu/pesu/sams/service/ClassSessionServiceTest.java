package edu.pesu.sams.service;

import edu.pesu.sams.dto.ClassSessionDto;
import edu.pesu.sams.dto.ClassSessionResponseDto;
import edu.pesu.sams.entity.*;
import edu.pesu.sams.exception.SamsException;
import edu.pesu.sams.repository.ClassSessionRepository;
import edu.pesu.sams.repository.CourseSectionRepository;
import edu.pesu.sams.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
class ClassSessionServiceTest {

    @Autowired UserAccountRepository users;
    @Autowired CourseSectionRepository sections;
    @Autowired ClassSessionRepository sessions;
    @Autowired ClassSessionService sessionService;

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

    private ClassSessionDto dto(Long sectionId, LocalDate date, LocalTime start, LocalTime end, SessionType type) {
        ClassSessionDto d = new ClassSessionDto();
        d.setCourseSectionId(sectionId);
        d.setDate(date);
        d.setStartTime(start);
        d.setEndTime(end);
        d.setType(type);
        return d;
    }

    @Test void facultyCanCreateSessionForAssignedSection() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        CourseSection sec = section("UE23CS341A", fac);

        ClassSessionResponseDto created = sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                fac.getEmail()
        );

        assertNotNull(created.getId());
        assertEquals(sec.getId(), created.getCourseSectionId());
        assertEquals(SessionType.LECTURE, created.getType());
        assertFalse(created.isSubmitted());
    }

    @Test void studentCannotCreateSession() {
        UserAccount stu = student("STU1001", "stu@sams.local");
        CourseSection sec = section("UE23CS341A", faculty("FAC001", "fac@sams.local"));

        assertThrows(SamsException.class, () -> sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                stu.getEmail()
        ));
    }

    @Test void facultyCannotCreateSessionForUnassignedSection() {
        UserAccount fac1 = faculty("FAC001", "fac1@sams.local");
        UserAccount fac2 = faculty("FAC002", "fac2@sams.local");
        CourseSection sec = section("UE23CS341A", fac1);

        assertThrows(SamsException.class, () -> sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                fac2.getEmail()
        ));
    }

    @Test void cannotCreateSessionForPastDate() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        CourseSection sec = section("UE23CS341A", fac);

        assertThrows(SamsException.class, () -> sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2020, 1, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                fac.getEmail()
        ));
    }

    @Test void cannotCreateSessionWithInvalidTimes() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        CourseSection sec = section("UE23CS341A", fac);

        assertThrows(SamsException.class, () -> sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(10, 0), LocalTime.of(9, 0), SessionType.LECTURE),
                fac.getEmail()
        ));
    }

    @Test void facultyCanViewOwnSession() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        CourseSection sec = section("UE23CS341A", fac);

        ClassSessionResponseDto created = sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                fac.getEmail()
        );

        ClassSessionResponseDto fetched = sessionService.getSession(created.getId(), fac.getEmail());
        assertEquals(created.getId(), fetched.getId());
    }

    @Test void facultyCannotViewOtherFacultySession() {
        UserAccount fac1 = faculty("FAC001", "fac1@sams.local");
        UserAccount fac2 = faculty("FAC002", "fac2@sams.local");
        CourseSection sec = section("UE23CS341A", fac1);

        ClassSessionResponseDto created = sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                fac1.getEmail()
        );

        assertThrows(SamsException.class, () -> sessionService.getSession(created.getId(), fac2.getEmail()));
    }

    @Test void facultyCanSubmitSession() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        CourseSection sec = section("UE23CS341A", fac);

        ClassSessionResponseDto created = sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                fac.getEmail()
        );

        ClassSessionResponseDto submitted = sessionService.submitSession(created.getId(), fac.getEmail());
        assertTrue(submitted.isSubmitted());
    }

    @Test void cannotSubmitAlreadySubmittedSession() {
        UserAccount fac = faculty("FAC001", "fac@sams.local");
        CourseSection sec = section("UE23CS341A", fac);

        ClassSessionResponseDto created = sessionService.createSession(
                dto(sec.getId(), LocalDate.of(2026, 10, 22), LocalTime.of(9, 0), LocalTime.of(10, 0), SessionType.LECTURE),
                fac.getEmail()
        );

        sessionService.submitSession(created.getId(), fac.getEmail());

        assertThrows(SamsException.class, () -> sessionService.submitSession(created.getId(), fac.getEmail()));
    }
}