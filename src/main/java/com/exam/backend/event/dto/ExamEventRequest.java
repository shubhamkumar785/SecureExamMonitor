package com.exam.backend.event.dto;

import java.time.Instant;

import com.exam.backend.event.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExamEventRequest {

    @NotBlank(message = "studentId is required")
    @Size(max = 100, message = "studentId must be at most 100 characters")
    private String studentId;

    @NotNull(message = "eventType is required")
    private EventType eventType;

    private Instant timestamp;

    @Size(max = 500, message = "details must be at most 500 characters")
    private String details;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
