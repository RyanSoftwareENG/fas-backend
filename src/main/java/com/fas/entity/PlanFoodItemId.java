package com.fas.entity;

import java.io.Serializable;
import java.util.Objects;

public class PlanFoodItemId implements Serializable {

    private Long nutritionPlan;
    private Long foodItem;
    private String mealType;

    public PlanFoodItemId() {
    }

    public PlanFoodItemId(
            Long nutritionPlan,
            Long foodItem,
            String mealType) {

        this.nutritionPlan = nutritionPlan;
        this.foodItem = foodItem;
        this.mealType = mealType;
    }

    public Long getNutritionPlan() {
        return nutritionPlan;
    }

    public void setNutritionPlan(Long nutritionPlan) {
        this.nutritionPlan = nutritionPlan;
    }

    public Long getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(Long foodItem) {
        this.foodItem = foodItem;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof PlanFoodItemId)) {
            return false;
        }

        PlanFoodItemId that =
                (PlanFoodItemId) o;

        return Objects.equals(
                nutritionPlan,
                that.nutritionPlan
        )
                && Objects.equals(
                foodItem,
                that.foodItem
        )
                && Objects.equals(
                mealType,
                that.mealType
        );
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                nutritionPlan,
                foodItem,
                mealType
        );
    }
}