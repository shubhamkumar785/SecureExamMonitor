package com.exam.backend.event;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.exam.backend.event.dto.ExamEventRequest;

import jakarta.validation.Valid;

@Controller
public class ExamEventSocketController {

    private final ExamEventService examEventService;

    public ExamEventSocketController(ExamEventService examEventService) {
        this.examEventService = examEventService;
    }

    @MessageMapping("/event")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public void receiveEvent(@Valid ExamEventRequest request) {
        examEventService.saveEvent(request);
        // The service broadcasts to /topic/alerts after persistence.
    }
}
