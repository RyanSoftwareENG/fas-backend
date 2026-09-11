package com.admin.repository;

import com.admin.entity.FasAdminUserRole;
import com.admin.entity.FasAdminUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FasAdminUserRoleRepository
        extends JpaRepository<FasAdminUserRole, FasAdminUserRoleId> {

    List<FasAdminUserRole> findByAdminUserId(
            Long adminUserId
    );

    List<FasAdminUserRole> findByAdminRoleId(
            Long adminRoleId
    );
}