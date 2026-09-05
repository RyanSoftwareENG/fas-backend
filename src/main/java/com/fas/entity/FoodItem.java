package com.fas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "FoodItem") // 1. تعديل اسم الجدول ليطابق SQL
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FoodItem_ID") // 2. ربط اسم المفتاح الأساسي
    private Long foodItemId;

    // 3. ربط الاسم وتحديد الطول
    @Column(name = "Food_Name", nullable = false, length = 100)
    private String foodName;

    @Column(name = "Calories", precision = 8, scale = 2)
    private BigDecimal calories;

    @Column(name = "Protein", precision = 8, scale = 2)
    private BigDecimal protein;

    @Column(name = "Carbohydrates", precision = 8, scale = 2)
    private BigDecimal carbohydrates;

    @Column(name = "Fat", precision = 8, scale = 2)
    private BigDecimal fat;

    // 4. حل مشكلة الـ boolean بربطه مع Y و N
    @Column(name = "Availability", length = 1)
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    private boolean available = true; // القيمة الافتراضية true تعادل 'Y'

    @Column(name = "Cost_Level", length = 20) // 5. ربط اسم العمود
    private String costLevel;

    // Constructors
    public FoodItem(){}

    public FoodItem(
            String foodName,
            BigDecimal calories,
            BigDecimal protein,
            BigDecimal carbohydrates,
            BigDecimal fat,
            boolean available,
            String costLevel
    ){
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.available = available;
        this.costLevel = costLevel;
    }

    // Getters and Setters

    public Long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCostLevel() {
        return costLevel;
    }

    public void setCostLevel(String costLevel) {
        this.costLevel = costLevel;
    }
}