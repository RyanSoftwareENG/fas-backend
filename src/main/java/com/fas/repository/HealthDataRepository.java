package com.fas.repository;

import com.fas.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthDataRepository
        extends JpaRepository<HealthData, Long> {

    @Query("""
        SELECT h
        FROM HealthData h
        WHERE h.client.clientID = :clientID
    """)
    Optional<HealthData> findByClientId(
            @Param("clientID") Long clientID
    );
}