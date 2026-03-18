package com.exam.backend.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamEventRepository extends JpaRepository<ExamEvent, Long> {
}
