package edu.pesu.sams.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name="course_sections", uniqueConstraints=@UniqueConstraint(name="uk_course_section_identity", columnNames={"course_code","semester","section_name","academic_year"}))
public class CourseSection {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="course_code", nullable=false, length=32) private String courseCode;
    @Column(name="course_name", nullable=false, length=160) private String courseName;
    @Column(nullable=false, length=24) private String semester;
    @Column(name="section_name", nullable=false, length=24) private String section;
    @Column(name="academic_year", nullable=false, length=16) private String academicYear;
    @Column(nullable=false) private boolean active=true;
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="course_faculty", joinColumns=@JoinColumn(name="course_section_id"), inverseJoinColumns=@JoinColumn(name="faculty_user_id"), uniqueConstraints=@UniqueConstraint(name="uk_course_faculty", columnNames={"course_section_id","faculty_user_id"}))
    private Set<UserAccount> faculty = new HashSet<>();
    public CourseSection() {}
    public Long getId(){return id;} public String getCourseCode(){return courseCode;} public void setCourseCode(String v){courseCode=v;}
    public String getCourseName(){return courseName;} public void setCourseName(String v){courseName=v;}
    public String getSemester(){return semester;} public void setSemester(String v){semester=v;}
    public String getSection(){return section;} public void setSection(String v){section=v;}
    public String getAcademicYear(){return academicYear;} public void setAcademicYear(String v){academicYear=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    public Set<UserAccount> getFaculty(){return faculty;} public void setFaculty(Set<UserAccount> v){faculty=v;}
}
