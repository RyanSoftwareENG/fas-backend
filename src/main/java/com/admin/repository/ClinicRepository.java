package com.admin.repository;

import com.admin.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicRepository
        extends JpaRepository<Clinic, Long> {

    Optional<Clinic> findByClinicCode(
            String clinicCode
    );

    boolean existsByClinicCode(
            String clinicCode
    );
}