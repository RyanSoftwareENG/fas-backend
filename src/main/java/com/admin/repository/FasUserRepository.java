package com.admin.repository;

import com.admin.entity.FasUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FasUserRepository
        extends JpaRepository<FasUser, Long> {

    Optional<FasUser> findByUsername(
            String username
    );

    boolean existsByUsername(
            String username
    );

    boolean existsByUsernameAndUserIdNot(
            String username,
            Long userId
    );

    List<FasUser> findByClinicId(
            Long clinicId
    );

    List<FasUser> findByClinicIdAndStatus(
            Long clinicId,
            String status
    );
    Optional<FasUser> findByUsernameAndStatus( String username, String status );
}