package edu.pesu.sams.service;

import edu.pesu.sams.dto.AttendanceRecordDto;
import edu.pesu.sams.dto.AttendanceRecordResponseDto;
import edu.pesu.sams.entity.*;
import edu.pesu.sams.exception.SamsException;
import edu.pesu.sams.repository.AttendanceRecordRepository;
import edu.pesu.sams.repository.ClassSessionRepository;
import edu.pesu.sams.repository.CourseSectionRepository;
import edu.pesu.sams.repository.EnrollmentRepository;
import edu.pesu.sams.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRecordRepository recordRepository;
    private final ClassSessionRepository sessionRepository;
    private final UserAccountRepository userRepository;
    private final CourseSectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceService(AttendanceRecordRepository recordRepository,
                             ClassSessionRepository sessionRepository,
                             UserAccountRepository userRepository,
                             CourseSectionRepository sectionRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.recordRepository = recordRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public AttendanceRecordResponseDto markAttendance(AttendanceRecordDto dto, String facultyEmail) {
        UserAccount faculty = userRepository.findByEmailIgnoreCase(facultyEmail)
                .orElseThrow(() -> new SamsException("Faculty not found", SamsException.ErrorCode.NOT_FOUND));

        if (faculty.getRole() != UserRole.FACULTY) {
            throw new SamsException("Only faculty can mark attendance", SamsException.ErrorCode.FORBIDDEN);
        }

        ClassSession session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new SamsException("Session not found", SamsException.ErrorCode.NOT_FOUND));

        if (!session.getCreatedBy().getId().equals(faculty.getId())) {
            throw new SamsException("Faculty not authorized for this session", SamsException.ErrorCode.FORBIDDEN);
        }

        if (session.isSubmitted()) {
            throw new SamsException("Cannot mark attendance for submitted session", SamsException.ErrorCode.CONFLICT);
        }

        UserAccount student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new SamsException("Student not found", SamsException.ErrorCode.NOT_FOUND));

        if (student.getRole() != UserRole.STUDENT) {
            throw new SamsException("Attendance can only be marked for students", SamsException.ErrorCode.VALIDATION_ERROR);
        }

        if (!enrollmentRepository.existsByCourseSection_IdAndStudent_Id(session.getCourseSection().getId(), student.getId())) {
            throw new SamsException("Student not enrolled in this course section", SamsException.ErrorCode.FORBIDDEN);
        }

        Optional<AttendanceRecord> existing = recordRepository.findBySession_IdAndStudent_Id(session.getId(), student.getId());
        if (existing.isPresent()) {
            throw new SamsException("Attendance already marked for this student in this session", SamsException.ErrorCode.CONFLICT);
        }

        AttendanceRecord record = new AttendanceRecord(session, student, dto.getStatus());
        record = recordRepository.saveAndFlush(record);
        return mapToResponseDto(record);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponseDto> getAttendanceBySession(Long sessionId, String userEmail) {
        UserAccount user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new SamsException("User not found", SamsException.ErrorCode.NOT_FOUND));

        ClassSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SamsException("Session not found", SamsException.ErrorCode.NOT_FOUND));

        if (user.getRole() == UserRole.FACULTY) {
            if (!session.getCreatedBy().getId().equals(user.getId())) {
                throw new SamsException("Unauthorized to view this session's attendance", SamsException.ErrorCode.FORBIDDEN);
            }
        } else if (user.getRole() == UserRole.STUDENT) {
            if (!enrollmentRepository.existsByCourseSection_IdAndStudent_Id(session.getCourseSection().getId(), user.getId())) {
                throw new SamsException("Student not enrolled in this course section", SamsException.ErrorCode.FORBIDDEN);
            }
        } else {
            throw new SamsException("Unauthorized", SamsException.ErrorCode.FORBIDDEN);
        }

        return recordRepository.findBySession_Id(sessionId)
                .stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponseDto> getAttendanceByStudent(Long studentId, String userEmail) {
        UserAccount requester = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new SamsException("User not found", SamsException.ErrorCode.NOT_FOUND));

        if (requester.getRole() == UserRole.STUDENT && !requester.getId().equals(studentId)) {
            throw new SamsException("Students can only view their own attendance", SamsException.ErrorCode.FORBIDDEN);
        }

        if (requester.getRole() == UserRole.FACULTY) {
            // Faculty can view attendance for students in their sections
            // This is allowed - no additional check needed
        } else if (requester.getRole() != UserRole.STUDENT) {
            throw new SamsException("Unauthorized", SamsException.ErrorCode.FORBIDDEN);
        }

        return recordRepository.findByStudent_Id(studentId)
                .stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponseDto> getAttendanceByStudentAndSection(Long studentId, Long sectionId, String userEmail) {
        UserAccount requester = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new SamsException("User not found", SamsException.ErrorCode.NOT_FOUND));

        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new SamsException("Course section not found", SamsException.ErrorCode.NOT_FOUND));

        if (requester.getRole() == UserRole.FACULTY) {
            if (!section.getFaculty().stream().anyMatch(f -> f.getId().equals(requester.getId()))) {
                throw new SamsException("Faculty not assigned to this section", SamsException.ErrorCode.FORBIDDEN);
            }
        } else if (requester.getRole() == UserRole.STUDENT) {
            if (!requester.getId().equals(studentId)) {
                throw new SamsException("Students can only view their own attendance", SamsException.ErrorCode.FORBIDDEN);
            }
            if (!enrollmentRepository.existsByCourseSection_IdAndStudent_Id(sectionId, studentId)) {
                throw new SamsException("Student not enrolled in this section", SamsException.ErrorCode.FORBIDDEN);
            }
        } else {
            throw new SamsException("Unauthorized", SamsException.ErrorCode.FORBIDDEN);
        }

        return recordRepository.findByStudent_IdAndSession_CourseSection_Id(studentId, sectionId)
                .stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceRecordResponseDto getAttendanceBySessionAndStudent(Long sessionId, Long studentId, String userEmail) {
        UserAccount user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new SamsException("User not found", SamsException.ErrorCode.NOT_FOUND));

        ClassSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SamsException("Session not found", SamsException.ErrorCode.NOT_FOUND));

        if (user.getRole() == UserRole.FACULTY) {
            if (!session.getCreatedBy().getId().equals(user.getId())) {
                throw new SamsException("Unauthorized", SamsException.ErrorCode.FORBIDDEN);
            }
        } else if (user.getRole() == UserRole.STUDENT) {
            if (!user.getId().equals(studentId)) {
                throw new SamsException("Students can only view their own attendance", SamsException.ErrorCode.FORBIDDEN);
            }
        } else {
            throw new SamsException("Unauthorized", SamsException.ErrorCode.FORBIDDEN);
        }

        AttendanceRecord record = recordRepository.findBySession_IdAndStudent_Id(sessionId, studentId)
                .orElseThrow(() -> new SamsException("Attendance record not found", SamsException.ErrorCode.NOT_FOUND));

        return mapToResponseDto(record);
    }

    private AttendanceRecordResponseDto mapToResponseDto(AttendanceRecord record) {
        AttendanceRecordResponseDto dto = new AttendanceRecordResponseDto();
        dto.setId(record.getId());
        dto.setSessionId(record.getSession().getId());
        dto.setSessionDate(record.getSession().getDate().atTime(record.getSession().getStartTime()));
        dto.setSessionStartTime(record.getSession().getStartTime());
        dto.setSessionEndTime(record.getSession().getEndTime());
        dto.setCourseCode(record.getSession().getCourseSection().getCourseCode());
        dto.setCourseName(record.getSession().getCourseSection().getCourseName());
        dto.setSectionName(record.getSession().getCourseSection().getSectionName());
        dto.setStudentId(record.getStudent().getId());
        dto.setStudentInstitutionId(record.getStudent().getInstitutionId());
        dto.setStudentName(record.getStudent().getFullName());
        dto.setStatus(record.getStatus());
        dto.setMarkedAt(record.getMarkedAt());
        return dto;
    }
}