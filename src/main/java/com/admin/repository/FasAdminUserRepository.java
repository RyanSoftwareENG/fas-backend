package com.admin.repository;

import com.admin.entity.FasAdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FasAdminUserRepository
        extends JpaRepository<FasAdminUser, Long> {

    Optional<FasAdminUser> findByUsername(String username);
}