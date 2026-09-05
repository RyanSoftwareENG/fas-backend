package com.fas.repository;

import com.fas.entity.SessionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionReportRepository
        extends JpaRepository<SessionReport, Long> {

    Optional<SessionReport> findBySessionId(Long sessionId);
}