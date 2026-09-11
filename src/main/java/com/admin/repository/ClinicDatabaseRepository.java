package com.admin.repository;

import com.admin.entity.ClinicDatabase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicDatabaseRepository
        extends JpaRepository<ClinicDatabase, Long> {

    Optional<ClinicDatabase> findByClinicClinicId(Long clinicId);

    Optional<ClinicDatabase> findBySchemaNameIgnoreCase(String schemaName);

    boolean existsByClinicClinicId(Long clinicId);

    boolean existsBySchemaNameIgnoreCase(String schemaName);
}