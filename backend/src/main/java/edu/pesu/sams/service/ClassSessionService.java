package edu.pesu.sams.service;

import edu.pesu.sams.dto.ClassSessionDto;
import edu.pesu.sams.dto.ClassSessionResponseDto;
import edu.pesu.sams.entity.ClassSession;
import edu.pesu.sams.entity.CourseSection;
import edu.pesu.sams.entity.UserAccount;
import edu.pesu.sams.entity.UserRole;
import edu.pesu.sams.exception.SamsException;
import edu.pesu.sams.repository.ClassSessionRepository;
import edu.pesu.sams.repository.CourseSectionRepository;
import edu.pesu.sams.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassSessionService {

    private final ClassSessionRepository sessionRepository;
    private final CourseSectionRepository sectionRepository;
    private final UserAccountRepository userRepository;

    public ClassSessionService(ClassSessionRepository sessionRepository,
                               CourseSectionRepository sectionRepository,
                               UserAccountRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
    }

    public ClassSessionResponseDto createSession(ClassSessionDto dto, String facultyEmail) {
        UserAccount faculty = userRepository.findByEmailIgnoreCase(facultyEmail)
                .orElseThrow(() -> new SamsException("Faculty not found", SamsException.ErrorCode.NOT_FOUND));

        if (faculty.getRole() != UserRole.FACULTY) {
            throw new SamsException("Only faculty can create sessions", SamsException.ErrorCode.FORBIDDEN);
        }

        CourseSection section = sectionRepository.findById(dto.getCourseSectionId())
                .orElseThrow(() -> new SamsException("Course section not found", SamsException.ErrorCode.NOT_FOUND));

        if (!section.getFaculty().stream().anyMatch(f -> f.getId().equals(faculty.getId()))) {
            throw new SamsException("Faculty not assigned to this course section", SamsException.ErrorCode.FORBIDDEN);
        }

        if (dto.getDate().isBefore(LocalDate.now())) {
            throw new SamsException("Cannot create session for past date", SamsException.ErrorCode.VALIDATION_ERROR);
        }

        if (dto.getEndTime().isBefore(dto.getStartTime()) || dto.getEndTime().equals(dto.getStartTime())) {
            throw new SamsException("End time must be after start time", SamsException.ErrorCode.VALIDATION_ERROR);
        }

        ClassSession session = new ClassSession();
        session.setCourseSection(section);
        session.setDate(dto.getDate());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setType(dto.getType());
        session.setCreatedBy(faculty);

        session = sessionRepository.saveAndFlush(session);
        return mapToResponseDto(session);
    }

    @Transactional(readOnly = true)
    public ClassSessionResponseDto getSession(Long sessionId, String facultyEmail) {
        ClassSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SamsException("Session not found", SamsException.ErrorCode.NOT_FOUND));

        if (!session.getCreatedBy().getEmail().equalsIgnoreCase(facultyEmail)) {
            throw new SamsException("Unauthorized to view this session", SamsException.ErrorCode.FORBIDDEN);
        }

        return mapToResponseDto(session);
    }

    @Transactional(readOnly = true)
    public List<ClassSessionResponseDto> getSessionsByFaculty(String facultyEmail, Pageable pageable) {
        UserAccount faculty = userRepository.findByEmailIgnoreCase(facultyEmail)
                .orElseThrow(() -> new SamsException("Faculty not found", SamsException.ErrorCode.NOT_FOUND));

        Page<ClassSession> sessions = sessionRepository.findByCreatedBy_Id(faculty.getId(), pageable);
        return sessions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassSessionResponseDto> getSessionsBySection(Long sectionId, String facultyEmail) {
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new SamsException("Course section not found", SamsException.ErrorCode.NOT_FOUND));

        if (!section.getFaculty().stream().anyMatch(f -> f.getEmail().equalsIgnoreCase(facultyEmail))) {
            throw new SamsException("Unauthorized to view sessions for this section", SamsException.ErrorCode.FORBIDDEN);
        }

        return sessionRepository.findByCourseSection_IdOrderByDateDescStartTimeDesc(sectionId)
                .stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    public ClassSessionResponseDto submitSession(Long sessionId, String facultyEmail) {
        ClassSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SamsException("Session not found", SamsException.ErrorCode.NOT_FOUND));

        if (!session.getCreatedBy().getEmail().equalsIgnoreCase(facultyEmail)) {
            throw new SamsException("Unauthorized to submit this session", SamsException.ErrorCode.FORBIDDEN);
        }

        if (session.isSubmitted()) {
            throw new SamsException("Session already submitted", SamsException.ErrorCode.CONFLICT);
        }

        session.setSubmitted(true);
        session = sessionRepository.saveAndFlush(session);
        return mapToResponseDto(session);
    }

    private ClassSessionResponseDto mapToResponseDto(ClassSession session) {
        ClassSessionResponseDto dto = new ClassSessionResponseDto();
        dto.setId(session.getId());
        dto.setCourseSectionId(session.getCourseSection().getId());
        dto.setCourseCode(session.getCourseSection().getCourseCode());
        dto.setCourseName(session.getCourseSection().getCourseName());
        dto.setSectionName(session.getCourseSection().getSectionName());
        dto.setDate(session.getDate());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setType(session.getType());
        dto.setCreatedByUserId(session.getCreatedBy().getId());
        dto.setCreatedByName(session.getCreatedBy().getFullName());
        dto.setSubmitted(session.isSubmitted());
        dto.setCreatedAt(session.getCreatedAt());
        return dto;
    }
}