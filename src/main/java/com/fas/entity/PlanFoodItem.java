package com.fas.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;

@Entity
@Table(name = "PlanFoodItem") // تم التعديل ليطابق SQL
@IdClass(PlanFoodItemId.class) // ربط الكيان بالمفتاح المركب
public class PlanFoodItem {

    // تم حذف المتغير id بالكامل

    @Id // جزء من المفتاح المركب
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NutritionPlan_ID", nullable = false) // تم مطابقة الاسم مع SQL
    @JsonIgnore
    private NutritionPlan nutritionPlan;

    @Id // جزء من المفتاح المركب
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FoodItem_ID", nullable = false) // تم مطابقة الاسم مع SQL
    private FoodItem foodItem;

    @Id // جزء من المفتاح المركب
    @Column(name = "Meal_Type", length = 50, nullable = false) // تم مطابقة الاسم والقيود
    private String mealType;

    @Column(name = "Quantity", nullable = false, precision = 8, scale = 2) // تم إضافة name و nullable
    private BigDecimal quantity;

    @Column(name = "Unit", length = 20) // تم تحديد الطول كما في SQL
    private String unit = "غرام";

    // Constructors
    public PlanFoodItem(){}

    public PlanFoodItem(
            NutritionPlan nutritionPlan,
            FoodItem foodItem,
            String mealType,
            BigDecimal quantity
    ){
        this.nutritionPlan = nutritionPlan;
        this.foodItem = foodItem;
        this.mealType = mealType;
        this.quantity = quantity;
    }

    // Getters and Setters (بدون getId)

    public NutritionPlan getNutritionPlan(){
        return nutritionPlan;
    }

    public void setNutritionPlan(NutritionPlan nutritionPlan){
        this.nutritionPlan = nutritionPlan;
    }

    public FoodItem getFoodItem(){
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem){
        this.foodItem = foodItem;
    }

    public String getMealType(){
        return mealType;
    }

    public void setMealType(String mealType){
        this.mealType = mealType;
    }

    public BigDecimal getQuantity(){
        return quantity;
    }

    public void setQuantity(BigDecimal quantity){
        this.quantity = quantity;
    }

    public String getUnit(){
        return unit;
    }

    public void setUnit(String unit){
        this.unit = unit;
    }
}