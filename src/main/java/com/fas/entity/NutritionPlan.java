package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NutritionPlan")
public class NutritionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Plan_ID")
    private Long planId;

    @OneToOne
    @JoinColumn(name = "Session_ID", nullable = false)
    @JsonIgnore
    private Session session;

    @Column(name = "Target_Goal")
    private String targetGoal;

    @Column(name = "Start_Date")
    private LocalDate startDate;

    @Column(name = "End_Date")
    private LocalDate endDate;

    // 🔴 تم إضافة هذا الحقل ليتطابق مع SQL (Plan_Duration NUMBER NOT NULL)
    @Column(name = "Plan_Duration", nullable = false)
    private int planDuration;

    @CreationTimestamp
    @Column(name = "Upload_Date", updatable = false)
    private LocalDateTime uploadDate;

    @UpdateTimestamp
    @Column(name = "Modification_Date")
    private LocalDateTime modificationDate;

    @Column(name = "Plan_Status")
    private String planStatus;

    @Column(name = "Meal_Distribution")
    private String mealDistribution;

    @Column(name = "Protein_Amount", precision = 8, scale = 2)
    private BigDecimal proteinAmount;

    @Column(name = "Fat_Amount", precision = 8, scale = 2)
    private BigDecimal fatAmount;

    @Column(name = "Carbohydrates_Amount", precision = 8, scale = 2)
    private BigDecimal carbohydratesAmount;

    @Column(name = "Total_Calories", precision = 8, scale = 2)
    private BigDecimal totalCalories;

    // العلاقة مع PlanFoodItem
    @OneToMany(mappedBy = "nutritionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanFoodItem> selectedFoods = new ArrayList<>();

    @Column(name = "Meals_Count")
    private Integer mealsCount;

    @Column(name = "Water_INTAKE")
    private String waterIntake;

    @Column(name = "Notes", length = 2000)
    private String notes;

    public NutritionPlan() {}

    public NutritionPlan(
            String targetGoal,
            LocalDate startDate,
            LocalDate endDate,
            String planStatus,
            String mealDistribution,
            BigDecimal proteinAmount,
            BigDecimal fatAmount,
            BigDecimal carbohydratesAmount
    ) {
        this.targetGoal = targetGoal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.planStatus = planStatus;
        this.mealDistribution = mealDistribution;
        this.proteinAmount = proteinAmount;
        this.fatAmount = fatAmount;
        this.carbohydratesAmount = carbohydratesAmount;
    }

    // ==========================================
    // Hibernate Lifecycle Callbacks (دوال تلقائية)
    // ==========================================

    // هذه الدالة ستعمل تلقائياً قبل أي عملية (Save) أو (Update) في قاعدة البيانات
    @PrePersist
    @PreUpdate
    public void calculateBeforeSave() {
        // 1. حساب مدة الخطة بالأيام لملء عمود Plan_Duration
        if (startDate != null && endDate != null) {
            this.planDuration = (int) ChronoUnit.DAYS.between(startDate, endDate);
        } else {
            this.planDuration = 0;
        }

        // 2. حساب إجمالي السعرات لملء عمود Total_Calories
        BigDecimal protein = proteinAmount != null ? proteinAmount.multiply(BigDecimal.valueOf(4)) : BigDecimal.ZERO;
        BigDecimal carbs = carbohydratesAmount != null ? carbohydratesAmount.multiply(BigDecimal.valueOf(4)) : BigDecimal.ZERO;
        BigDecimal fat = fatAmount != null ? fatAmount.multiply(BigDecimal.valueOf(9)) : BigDecimal.ZERO;
        this.totalCalories = protein.add(carbs).add(fat);
    }

    // ==========================================
    // الدوال الحسابية الخارجية
    // ==========================================

    @Transient // إجبار Hibernate على تجاهل هذه الدالة وعدم اعتبارها عموداً في قاعدة البيانات
    public BigDecimal calculateIdealWater(BigDecimal weight, String gender) {
        boolean male = "Male".equalsIgnoreCase(gender) || "ذكر".equals(gender) || "M".equalsIgnoreCase(gender);
        BigDecimal factor = male ? BigDecimal.valueOf(0.04) : BigDecimal.valueOf(0.035);
        return weight.multiply(factor);
    }

    // ==========================================
    // Getters and Setters
    // ==========================================

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public String getTargetGoal() { return targetGoal; }
    public void setTargetGoal(String targetGoal) { this.targetGoal = targetGoal; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getPlanDuration() { return planDuration; }
    public void setPlanDuration(int planDuration) { this.planDuration = planDuration; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public LocalDateTime getModificationDate() { return modificationDate; }
    public void setModificationDate(LocalDateTime modificationDate) { this.modificationDate = modificationDate; }

    public String getPlanStatus() { return planStatus; }
    public void setPlanStatus(String planStatus) { this.planStatus = planStatus; }

    public String getMealDistribution() { return mealDistribution; }
    public void setMealDistribution(String mealDistribution) { this.mealDistribution = mealDistribution; }

    public BigDecimal getProteinAmount() { return proteinAmount; }
    public void setProteinAmount(BigDecimal proteinAmount) { this.proteinAmount = proteinAmount; }

    public BigDecimal getFatAmount() { return fatAmount; }
    public void setFatAmount(BigDecimal fatAmount) { this.fatAmount = fatAmount; }

    public BigDecimal getCarbohydratesAmount() { return carbohydratesAmount; }
    public void setCarbohydratesAmount(BigDecimal carbohydratesAmount) { this.carbohydratesAmount = carbohydratesAmount; }

    public BigDecimal getTotalCalories() { return totalCalories; }
    public void setTotalCalories(BigDecimal totalCalories) { this.totalCalories = totalCalories; }

    public List<PlanFoodItem> getSelectedFoods() { return selectedFoods; }
    public void setSelectedFoods(List<PlanFoodItem> selectedFoods) { this.selectedFoods = selectedFoods; }

    public Integer getMealsCount() { return mealsCount; }
    public void setMealsCount(Integer mealsCount) { this.mealsCount = mealsCount; }

    public String getWaterIntake() { return waterIntake; }
    public void setWaterIntake(String waterIntake) { this.waterIntake = waterIntake; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}