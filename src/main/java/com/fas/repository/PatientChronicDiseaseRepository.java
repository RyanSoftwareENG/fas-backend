package com.fas.repository;

import com.fas.entity.PatientChronicDisease;
import com.fas.entity.PatientChronicDiseaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientChronicDiseaseRepository extends JpaRepository<PatientChronicDisease, PatientChronicDiseaseId> {

    @Query("""
    SELECT ccd
    FROM PatientChronicDisease ccd
    JOIN FETCH ccd.chronicDisease 
    WHERE ccd.client.clientID = :clientID
""")
    List<PatientChronicDisease> findByClientId(@Param("clientID") Long clientID);
    void deleteByClient_ClientID(Long clientId);
}