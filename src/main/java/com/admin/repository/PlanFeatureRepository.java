package com.admin.repository;

import com.admin.entity.PlanFeature;
import com.admin.entity.PlanFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanFeatureRepository
        extends JpaRepository<PlanFeature, PlanFeatureId> {

    List<PlanFeature> findByPlanId(
            Long planId
    );

    List<PlanFeature> findByFeatureId(
            Long featureId
    );

    Optional<PlanFeature> findByPlanIdAndFeatureId(
            Long planId,
            Long featureId
    );

    boolean existsByPlanIdAndFeatureId(
            Long planId,
            Long featureId
    );

    void deleteByPlanIdAndFeatureId(
            Long planId,
            Long featureId
    );

    List<PlanFeature> findByPlanIdAndEnabled(
            Long planId,
            Boolean enabled
    );

    @Query("""
            SELECT pf
            FROM PlanFeature pf
            WHERE pf.planId = :planId
              AND pf.enabled = true
            """)
    List<PlanFeature> findEnabledFeaturesByPlanId(
            @Param("planId") Long planId
    );
}