package com.admin.repository;

import com.admin.entity.SubscriptionActivation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionActivationRepository
        extends JpaRepository<SubscriptionActivation, Long> {

    List<SubscriptionActivation>
    findBySubscriptionIdOrderByActivationDateDesc(
            Long subscriptionId
    );

    List<SubscriptionActivation>
    findByAdminUserIdOrderByActivationDateDesc(
            Long adminUserId
    );

    Optional<SubscriptionActivation>
    findFirstBySubscriptionIdOrderByActivationDateDesc(
            Long subscriptionId
    );
}