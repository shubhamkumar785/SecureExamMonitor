package com.exam.backend.event;

import com.exam.backend.event.dto.ExamEventRequest;
import com.exam.backend.event.dto.ExamEventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class ExamEventController {

    private final ExamEventService examEventService;

    public ExamEventController(ExamEventService examEventService) {
        this.examEventService = examEventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ExamEventResponse createEvent(@Valid @RequestBody ExamEventRequest request) {
        return examEventService.saveEvent(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExamEventResponse> getAllEvents() {
        return examEventService.getAllEvents();
    }
}
