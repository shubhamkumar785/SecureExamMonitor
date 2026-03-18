package com.exam.backend.event;

import java.time.Instant;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.backend.event.dto.ExamEventRequest;
import com.exam.backend.event.dto.ExamEventResponse;
import com.exam.backend.shared.BadRequestException;

@Service
public class ExamEventService {

    private final ExamEventRepository examEventRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ExamEventService(ExamEventRepository examEventRepository, SimpMessagingTemplate messagingTemplate) {
        this.examEventRepository = examEventRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public ExamEventResponse saveEvent(ExamEventRequest request) {
        validateRequest(request);

        ExamEvent event = new ExamEvent();
        event.setStudentId(request.getStudentId().trim());
        event.setEventType(request.getEventType());
        event.setTimestamp(request.getTimestamp() != null ? request.getTimestamp() : Instant.now());
        event.setDetails(request.getDetails() != null ? request.getDetails().trim() : null);

        ExamEvent persisted = examEventRepository.save(event);
        ExamEventResponse response = mapToResponse(persisted);

        messagingTemplate.convertAndSend("/topic/alerts", response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<ExamEventResponse> getAllEvents() {
        List<ExamEvent> events = examEventRepository.findAll();
        return events.stream().map(this::mapToResponse).toList();
    }

    private void validateRequest(ExamEventRequest request) {
        if (request.getEventType() == null) {
            throw new BadRequestException("eventType is required");
        }
        if (request.getStudentId() == null || request.getStudentId().trim().isEmpty()) {
            throw new BadRequestException("studentId is required");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Authenticated user is required");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!isAdmin && !authentication.getName().equals(request.getStudentId().trim())) {
            throw new BadRequestException("Students can submit events only for their own account");
        }

        if (request.getTimestamp() != null && request.getTimestamp().isAfter(Instant.now().plusSeconds(300))) {
            throw new BadRequestException("timestamp cannot be more than 5 minutes in the future");
        }
    }

    private ExamEventResponse mapToResponse(ExamEvent event) {
        return new ExamEventResponse(
                event.getId(),
                event.getStudentId(),
                event.getEventType(),
                event.getTimestamp(),
                event.getDetails());
    }
}
