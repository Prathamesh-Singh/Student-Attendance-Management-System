package edu.pesu.sams.controller;

import edu.pesu.sams.dto.AttendanceRecordDto;
import edu.pesu.sams.dto.AttendanceRecordResponseDto;
import edu.pesu.sams.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<AttendanceRecordResponseDto> markAttendance(
            @Valid @RequestBody AttendanceRecordDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        AttendanceRecordResponseDto response = attendanceService.markAttendance(dto, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceRecordResponseDto>> getAttendanceBySession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AttendanceRecordResponseDto> records = attendanceService.getAttendanceBySession(sessionId, userDetails.getUsername());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceRecordResponseDto>> getAttendanceByStudent(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AttendanceRecordResponseDto> records = attendanceService.getAttendanceByStudent(studentId, userDetails.getUsername());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/student/{studentId}/section/{sectionId}")
    public ResponseEntity<List<AttendanceRecordResponseDto>> getAttendanceByStudentAndSection(
            @PathVariable Long studentId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AttendanceRecordResponseDto> records = attendanceService.getAttendanceByStudentAndSection(studentId, sectionId, userDetails.getUsername());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/session/{sessionId}/student/{studentId}")
    public ResponseEntity<AttendanceRecordResponseDto> getAttendanceBySessionAndStudent(
            @PathVariable Long sessionId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        AttendanceRecordResponseDto record = attendanceService.getAttendanceBySessionAndStudent(sessionId, studentId, userDetails.getUsername());
        return ResponseEntity.ok(record);
    }
}