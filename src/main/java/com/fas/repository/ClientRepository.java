package com.fas.repository;

import com.fas.dto.ClientListDTO;
import com.fas.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // ==========================================
    // البيانات الأساسية فقط
    // ==========================================

    @Query("""
        SELECT new com.fas.dto.ClientListDTO(
            c.clientID,
            c.firstName,
            c.lastName,
            c.gender,
            c.birthDate,
            c.contactNumber
        )
        FROM Client c
    """)
    List<ClientListDTO> findAllClientBasicData();


    // ==========================================
    // جلب العميل + HealthData + Lifestyle
    // ==========================================

    @Query("""
        SELECT DISTINCT c
        FROM Client c
        LEFT JOIN FETCH c.healthData
        LEFT JOIN FETCH c.lifeStyleInformation
        WHERE c.clientID = :clientID
    """)
    Optional<Client> findClientWithBasicDetails(Long clientID);


    // ==========================================
    // جلب Allergies بشكل منفصل
    // ==========================================

    @Query("""
        SELECT c
        FROM Client c
        LEFT JOIN FETCH c.allergies
        WHERE c.clientID = :clientID
    """)
    Optional<Client> findClientWithAllergies(Long clientID);


    // ==========================================
    // جلب Chronic Diseases بشكل منفصل
    // ==========================================

    @Query("""
        SELECT c
        FROM Client c
        LEFT JOIN FETCH c.chronicDiseases
        WHERE c.clientID = :clientID
    """)
    Optional<Client> findClientWithChronicDiseases(Long clientID);


    // ==========================================
    // جلب Sessions بشكل منفصل
    // ==========================================

    @Query("""
        SELECT c
        FROM Client c
        LEFT JOIN FETCH c.sessions
        WHERE c.clientID = :clientID
    """)
    Optional<Client> findClientWithSessions(Long clientID);
}