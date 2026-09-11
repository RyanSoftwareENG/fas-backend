package com.admin.repository;

import com.admin.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository
        extends JpaRepository<Device, Long> {

    Optional<Device> findByInstallationId(
            String installationId
    );

    List<Device> findByClinicIdOrderByLastSeenAtDesc(
            Long clinicId
    );

    List<Device> findAllByOrderByLastSeenAtDesc();

    long countByStatus(
            String status
    );

    long countByClinicIdAndStatus(
            Long clinicId,
            String status
    );
}