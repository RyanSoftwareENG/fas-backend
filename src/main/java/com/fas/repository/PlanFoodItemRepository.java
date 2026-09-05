package com.fas.repository;

import com.fas.entity.PlanFoodItem;
import com.fas.entity.PlanFoodItemId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanFoodItemRepository
        extends JpaRepository<PlanFoodItem, PlanFoodItemId> {
    @EntityGraph(attributePaths = {"foodItem"})
    List<PlanFoodItem> findByNutritionPlanPlanId(Long planId);
}