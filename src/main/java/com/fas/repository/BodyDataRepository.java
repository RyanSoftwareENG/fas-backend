package com.fas.repository;

import com.fas.entity.BodyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BodyDataRepository extends JpaRepository<BodyData, Long> {
    Optional<BodyData> findBySessionId(Long sessionId);

}