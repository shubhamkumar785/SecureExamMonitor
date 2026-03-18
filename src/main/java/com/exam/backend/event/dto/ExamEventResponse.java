package com.exam.backend.event.dto;

import com.exam.backend.event.EventType;

import java.time.Instant;

public class ExamEventResponse {

    private final Long id;
    private final String studentId;
    private final EventType eventType;
    private final Instant timestamp;
    private final String details;

    public ExamEventResponse(Long id, String studentId, EventType eventType, Instant timestamp, String details) {
        this.id = id;
        this.studentId = studentId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }
}
