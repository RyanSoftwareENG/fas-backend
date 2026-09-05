package com.fas.repository;

import com.fas.dto.SessionListDTO;
import com.fas.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    // =========================================================
    // جلب جميع الجلسات مع بيانات العميل الأساسية
    // =========================================================

    @Query("""
    SELECT new com.fas.dto.SessionListDTO(
        s.id,
        s.uploadTime,
        s.duration,
        s.price,
        s.notes,

        c.clientID,
        c.firstName,
        c.lastName,
        c.gender,
        c.birthDate,
        c.contactNumber,
        c.uploadDate,
        c.modificationDate
    )
    FROM Session s
    JOIN s.client c
    ORDER BY s.uploadTime DESC
""")
    List<SessionListDTO> findAllSessionBasicData();


    // =========================================================
    // جلب جلسات عميل محدد
    // =========================================================

    @Query("""
        SELECT s
        FROM Session s
        WHERE s.client.clientID = :clientID
        ORDER BY s.uploadTime DESC
    """)
    List<Session> findByClientId(
            @Param("clientID") Long clientID
    );


    @Query("""
        SELECT s
        FROM Session s
        WHERE s.id = :id
    """)
    Optional<Session> findSessionOnly(@Param("id") Long id);
}