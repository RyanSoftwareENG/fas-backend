package com.admin.repository;

import com.admin.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository
        extends JpaRepository<Feature, Long> {

    Optional<Feature> findByFeatureCode(
            String featureCode
    );

    boolean existsByFeatureCode(
            String featureCode
    );

    List<Feature> findByStatus(
            String status
    );
}