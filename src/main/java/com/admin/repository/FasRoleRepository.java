package com.admin.repository;

import com.admin.entity.FasRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FasRoleRepository
        extends JpaRepository<FasRole, Long> {

    List<FasRole> findByStatusOrderByRoleIdAsc(
            String status
    );

    Optional<FasRole> findByRoleNameIgnoreCase(
            String roleName
    );
}