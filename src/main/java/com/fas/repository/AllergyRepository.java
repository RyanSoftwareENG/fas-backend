package com.fas.repository;

import com.fas.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllergyRepository
        extends JpaRepository<Allergy, Long> {

    Optional<Allergy>
    findByAllergyNameIgnoreCase(String allergyName);
}