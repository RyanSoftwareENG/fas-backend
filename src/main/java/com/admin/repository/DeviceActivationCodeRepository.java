package com.admin.repository;

import com.admin.entity.DeviceActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceActivationCodeRepository
        extends JpaRepository<DeviceActivationCode, Long> {

    Optional<DeviceActivationCode>
    findByCodeHashAndStatus(
            String codeHash,
            String status
    );

    List<DeviceActivationCode>
    findBySubscriptionIdOrderByCreatedAtDesc(
            Long subscriptionId
    );

    List<DeviceActivationCode>
    findByClinicIdOrderByCreatedAtDesc(
            Long clinicId
    );
}
