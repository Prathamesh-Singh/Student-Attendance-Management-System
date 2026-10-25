package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity @Table(name="enrolments", uniqueConstraints=@UniqueConstraint(name="uk_enrolment_section_student", columnNames={"course_section_id","student_user_id"}))
public class Enrollment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="course_section_id", nullable=false) private CourseSection courseSection;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="student_user_id", nullable=false) private UserAccount student;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null)createdAt=LocalDateTime.now(ZoneId.of("Asia/Kolkata")); }
    public Enrollment() {}
    public Enrollment(CourseSection courseSection, UserAccount student){this.courseSection=courseSection;this.student=student;}
    public Long getId(){return id;} public CourseSection getCourseSection(){return courseSection;} public UserAccount getStudent(){return student;} public LocalDateTime getCreatedAt(){return createdAt;}
}
