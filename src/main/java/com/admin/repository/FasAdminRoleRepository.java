package com.admin.repository;

import com.admin.entity.FasAdminRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FasAdminRoleRepository
        extends JpaRepository<FasAdminRole, Long> {

    Optional<FasAdminRole> findByRoleName(String roleName);
}