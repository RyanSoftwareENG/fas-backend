package com.fas.repository;

import com.fas.entity.LifeStyleInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LifeStyleInformationRepository
        extends JpaRepository<LifeStyleInformation, Long> {

    @Query("""
        SELECT l
        FROM LifeStyleInformation l
        WHERE l.client.clientID = :clientID
    """)
    Optional<LifeStyleInformation> findByClientId(
            @Param("clientID") Long clientID
    );
}