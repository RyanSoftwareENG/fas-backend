package com.admin.repository;

import com.admin.entity.SoftwareVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SoftwareVersionRepository
        extends JpaRepository<SoftwareVersion, Long> {

    List<SoftwareVersion> findByApplicationNameOrderByReleaseDateDesc(
            String applicationName
    );

    List<SoftwareVersion> findByStatusOrderByReleaseDateDesc(
            String status
    );

    Optional<SoftwareVersion>
    findFirstByApplicationNameAndStatusOrderByReleaseDateDesc(
            String applicationName,
            String status
    );

    Optional<SoftwareVersion>
    findFirstByApplicationNameOrderByReleaseDateDesc(
            String applicationName
    );
}