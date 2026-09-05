package com.fas.repository;

import com.fas.entity.NutritionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface NutritionPlanRepository
        extends JpaRepository<NutritionPlan, Long> {

    @Query(
            value = """
                SELECT *
                FROM NutritionPlan
                WHERE Session_ID = :sessionId
                """,
            nativeQuery = true
    )
    Optional<NutritionPlan> findBySessionId(
            @Param("sessionId") Long sessionId
    );
}