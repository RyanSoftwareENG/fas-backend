package com.admin.repository;

import com.admin.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findByPlanName(
            String planName
    );

    boolean existsByPlanName(
            String planName
    );

    List<SubscriptionPlan> findByStatus(
            String status
    );
}