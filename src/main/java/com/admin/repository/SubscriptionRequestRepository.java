package com.admin.repository;

import com.admin.entity.SubscriptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRequestRepository
        extends JpaRepository<SubscriptionRequest, Long> {

    boolean existsByClinicIdAndStatus(
            Long clinicId,
            String status
    );

    Optional<SubscriptionRequest>
    findByRequestIdAndClinicId(
            Long requestId,
            Long clinicId
    );

    List<SubscriptionRequest>
    findByClinicIdOrderByRequestedAtDesc(
            Long clinicId
    );

    List<SubscriptionRequest>
    findByStatusOrderByRequestedAtAsc(
            String status
    );
}