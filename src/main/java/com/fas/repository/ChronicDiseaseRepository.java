package com.fas.repository;

import com.fas.entity.ChronicDisease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChronicDiseaseRepository
        extends JpaRepository<ChronicDisease, Long> {

    Optional<ChronicDisease>
    findByDiseaseNameIgnoreCase(String diseaseName);
}