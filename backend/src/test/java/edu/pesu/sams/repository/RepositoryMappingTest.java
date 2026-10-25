package edu.pesu.sams.repository;

import edu.pesu.sams.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Checks entity mappings, derived queries and uniqueness rules on an in-memory database built from the entities. */
@DataJpaTest(properties={"spring.flyway.enabled=false","spring.jpa.hibernate.ddl-auto=create-drop"})
class RepositoryMappingTest {
 @Autowired UserAccountRepository users; @Autowired CourseSectionRepository sections; @Autowired EnrollmentRepository enrolments;
 @Autowired ClassSessionRepository sessions; @Autowired AttendanceRecordRepository records;

 private UserAccount user(String institutionId,String email,UserRole role){UserAccount u=new UserAccount();u.setInstitutionId(institutionId);u.setFullName(institutionId+" Name");u.setEmail(email);u.setRole(role);u.setPasswordHash("$2a$12$placeholderhashplaceholderhashplaceholderhashplacehold");return users.saveAndFlush(u);}
 private CourseSection section(String code,UserAccount... faculty){CourseSection s=new CourseSection();s.setCourseCode(code);s.setCourseName("Software Engineering");s.setSemester("5");s.setSection("A");s.setAcademicYear("2026-27");s.setFaculty(new HashSet<>(List.of(faculty)));return sections.saveAndFlush(s);}
 private ClassSession session(CourseSection s,UserAccount faculty){ClassSession c=new ClassSession();c.setCourseSection(s);c.setDate(LocalDate.of(2026,10,22));c.setStartTime(LocalTime.of(9,0));c.setEndTime(LocalTime.of(10,0));c.setType(SessionType.LECTURE);c.setCreatedBy(faculty);return sessions.saveAndFlush(c);}

 @Test void userLookupsIgnoreCaseAndSetTimestamps(){
   UserAccount saved=user("STU1001","asha@example.edu",UserRole.STUDENT);
   assertNotNull(saved.getId());assertNotNull(saved.getCreatedAt());assertTrue(saved.isActive());
   assertTrue(users.findByEmailIgnoreCase("ASHA@example.edu").isPresent());
   assertTrue(users.existsByInstitutionIdIgnoreCase("stu1001"));
   assertEquals(1,users.findByRoleAndActiveTrue(UserRole.STUDENT).size());
 }
 @Test void duplicateEmailIsRejected(){
   user("STU1001","asha@example.edu",UserRole.STUDENT);
   assertThrows(DataIntegrityViolationException.class,()->user("STU1002","asha@example.edu",UserRole.STUDENT));
 }
 @Test void duplicateInstitutionIdIsRejected(){
   user("STU1001","asha@example.edu",UserRole.STUDENT);
   assertThrows(DataIntegrityViolationException.class,()->user("STU1001","other@example.edu",UserRole.STUDENT));
 }
 @Test void duplicateCourseSectionIdentityIsRejected(){
   section("UE23CS341A");
   assertThrows(DataIntegrityViolationException.class,()->section("UE23CS341A"));
 }
 @Test void facultyAssignmentDrivesSectionLookup(){
   UserAccount faculty=user("FAC001","fac@sams.local",UserRole.FACULTY);section("UE23CS341A",faculty);section("UE23CS342A");
   List<CourseSection> assigned=sections.findDistinctByFaculty_EmailIgnoreCaseAndActiveTrue("FAC@sams.local");
   assertEquals(1,assigned.size());assertEquals("UE23CS341A",assigned.get(0).getCourseCode());
 }
 @Test void enrolmentIsUniquePerSectionAndStudent(){
   UserAccount student=user("STU1001","asha@example.edu",UserRole.STUDENT);CourseSection s=section("UE23CS341A");
   enrolments.saveAndFlush(new Enrollment(s,student));
   assertTrue(enrolments.existsByCourseSection_IdAndStudent_Id(s.getId(),student.getId()));
   assertEquals(1,enrolments.countByCourseSection_Id(s.getId()));
   assertThrows(DataIntegrityViolationException.class,()->enrolments.saveAndFlush(new Enrollment(s,student)));
 }
 @Test void attendanceIsUniquePerSessionAndStudent(){
   UserAccount faculty=user("FAC001","fac@sams.local",UserRole.FACULTY),student=user("STU1001","asha@example.edu",UserRole.STUDENT);
   ClassSession c=session(section("UE23CS341A",faculty),faculty);
   AttendanceRecord r=records.saveAndFlush(new AttendanceRecord(c,student,AttendanceStatus.PRESENT));
   assertNotNull(r.getMarkedAt());assertTrue(records.findBySession_IdAndStudent_Id(c.getId(),student.getId()).isPresent());
   assertThrows(DataIntegrityViolationException.class,()->records.saveAndFlush(new AttendanceRecord(c,student,AttendanceStatus.ABSENT)));
 }
}
