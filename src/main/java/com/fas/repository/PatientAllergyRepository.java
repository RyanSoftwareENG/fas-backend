package com.fas.repository;

import com.fas.entity.PatientAllergy;
import com.fas.entity.PatientAllergyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientAllergyRepository extends JpaRepository<PatientAllergy, PatientAllergyId> {

    @Query("""
        SELECT p
        FROM PatientAllergy p
        JOIN FETCH p.allergy 
        WHERE p.client.clientID = :clientID
    """)
    List<PatientAllergy> findByClientId(
            @Param("clientID") Long clientID
    );
    void deleteByClient_ClientID(Long clientId);
}