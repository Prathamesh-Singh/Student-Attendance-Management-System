package edu.pesu.sams.controller;

import edu.pesu.sams.dto.ClassSessionDto;
import edu.pesu.sams.dto.ClassSessionResponseDto;
import edu.pesu.sams.service.ClassSessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class ClassSessionController {

    private final ClassSessionService sessionService;

    public ClassSessionController(ClassSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<ClassSessionResponseDto> createSession(
            @Valid @RequestBody ClassSessionDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        ClassSessionResponseDto response = sessionService.createSession(dto, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ClassSessionResponseDto> getSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ClassSessionResponseDto response = sessionService.getSession(sessionId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClassSessionResponseDto>> getMySessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        List<ClassSessionResponseDto> sessions = sessionService.getSessionsByFaculty(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<ClassSessionResponseDto>> getSessionsBySection(
            @PathVariable Long sectionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ClassSessionResponseDto> sessions = sessionService.getSessionsBySection(sectionId, userDetails.getUsername());
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<ClassSessionResponseDto> submitSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ClassSessionResponseDto response = sessionService.submitSession(sessionId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}