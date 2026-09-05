package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Body_Data")
public class BodyData {

    // 1. المفتاح الأساسي والمرتبط برابطة OneToOne مع الجلسة
    @Id
    @Column(name = "Session_ID")
    private Long sessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "Session_ID")
    @JsonIgnore
    private Session session;

    // 2. العلاقة الإجبارية مع العميل (Client_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Client_ID", nullable = false)
    @JsonIgnore
    private Client client;

    // 3. تاريخ القياس (Measurement_Date) المتوافق مع SQL
    @CreationTimestamp
    @Column(name = "Measurement_Date", nullable = false, updatable = false)
    private LocalDate measurementDate;

    @Column(name = "Height", precision = 5, scale = 2, nullable = false)
    private BigDecimal height = BigDecimal.ZERO;

    @Column(name = "Weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal weight = BigDecimal.ZERO;

    @Column(name = "Body_Fat", precision = 5, scale = 2)
    private BigDecimal bodyFatPercentage;

    @Column(name = "Activity_Factor", precision = 4, scale = 2)
    private BigDecimal activityFactor = BigDecimal.valueOf(1.20);

    @Column(name = "SMM", precision = 5, scale = 2)
    private BigDecimal smm;

    @Column(name = "Muscle_Mass", precision = 5, scale = 2)
    private BigDecimal muscleMass;

    @Column(name = "Physical_Activity", length = 100)
    private String physicalActivity;

    @Column(name = "Arm_C", precision = 5, scale = 2)
    private BigDecimal arm_C;

    @Column(name = "Chest_C", precision = 5, scale = 2)
    private BigDecimal chest_C;

    @Column(name = "Waist_C", precision = 5, scale = 2)
    private BigDecimal waist_C;

    @Column(name = "Abdominal_C", precision = 5, scale = 2)
    private BigDecimal abdominal_C;

    @Column(name = "Hip_C", precision = 5, scale = 2)
    private BigDecimal hip_C;

    @Column(name = "Mid_Thigh_C", precision = 5, scale = 2)
    private BigDecimal midThigh_C;

    @Column(name = "Calf_C", precision = 5, scale = 2)
    private BigDecimal calf_C;

    // Constructor فارغ متوافق مع Hibernate
    public BodyData() {}

    private BodyData(Builder builder) {
        this.session = builder.session;
        this.client = builder.client;
        this.height = builder.height;
        this.weight = builder.weight;
        this.bodyFatPercentage = builder.bodyFatPercentage;
        this.activityFactor = builder.activityFactor;
        this.smm = builder.smm;
        this.muscleMass = builder.muscleMass;
        this.physicalActivity = builder.physicalActivity;
        this.arm_C = builder.arm_C;
        this.chest_C = builder.chest_C;
        this.waist_C = builder.waist_C;
        this.abdominal_C = builder.abdominal_C;
        this.hip_C = builder.hip_C;
        this.midThigh_C = builder.midThigh_C;
        this.calf_C = builder.calf_C;
    }

    // ==========================================
    // Builder Pattern
    // ==========================================
    public static class Builder {
        private Session session;
        private Client client;
        private BigDecimal height = BigDecimal.ZERO;
        private BigDecimal weight = BigDecimal.ZERO;
        private BigDecimal bodyFatPercentage = BigDecimal.ZERO;
        private BigDecimal activityFactor = BigDecimal.valueOf(1.20);
        private BigDecimal smm = BigDecimal.ZERO;
        private BigDecimal muscleMass = BigDecimal.ZERO;
        private String physicalActivity = "None";
        private BigDecimal arm_C = BigDecimal.ZERO;
        private BigDecimal chest_C = BigDecimal.ZERO;
        private BigDecimal waist_C = BigDecimal.ZERO;
        private BigDecimal abdominal_C = BigDecimal.ZERO;
        private BigDecimal hip_C = BigDecimal.ZERO;
        private BigDecimal midThigh_C = BigDecimal.ZERO;
        private BigDecimal calf_C = BigDecimal.ZERO;

        public Builder() {}

        public Builder session(Session session) { this.session = session; return this; }
        public Builder client(Client client) { this.client = client; return this; }
        public Builder height(BigDecimal height) { this.height = height; return this; }
        public Builder weight(BigDecimal weight) { this.weight = weight; return this; }
        public Builder bodyFatPercentage(BigDecimal bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; return this; }
        public Builder activityFactor(BigDecimal activityFactor) { this.activityFactor = activityFactor; return this; }
        public Builder smm(BigDecimal smm) { this.smm = smm; return this; }
        public Builder muscleMass(BigDecimal muscleMass) { this.muscleMass = muscleMass; return this; }
        public Builder physicalActivity(String physicalActivity) { this.physicalActivity = physicalActivity; return this; }
        public Builder arm_C(BigDecimal arm_C) { this.arm_C = arm_C; return this; }
        public Builder chest_C(BigDecimal chest_C) { this.chest_C = chest_C; return this; }
        public Builder waist_C(BigDecimal waist_C) { this.waist_C = waist_C; return this; }
        public Builder abdominal_C(BigDecimal abdominal_C) { this.abdominal_C = abdominal_C; return this; }
        public Builder hip_C(BigDecimal hip_C) { this.hip_C = hip_C; return this; }
        public Builder midThigh_C(BigDecimal midThigh_C) { this.midThigh_C = midThigh_C; return this; }
        public Builder calf_C(BigDecimal calf_C) { this.calf_C = calf_C; return this; }

        public BodyData build() { return new BodyData(this); }
    }

    // ==========================================
    // Transient Helper Methods (الدوال الحسابية)
    // ==========================================

    @Transient
    public BigDecimal getBMI() {
        if (weight == null || height == null ||
                weight.compareTo(BigDecimal.ZERO) == 0 ||
                height.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal bmi = weight
                .multiply(BigDecimal.valueOf(10000))
                .divide(height.multiply(height), 2, java.math.RoundingMode.HALF_UP);

        if (bmi.compareTo(BigDecimal.valueOf(10)) < 0 ||
                bmi.compareTo(BigDecimal.valueOf(80)) > 0) {
            return BigDecimal.ZERO;
        }

        return bmi;
    }

    @Transient
    public String getStatus() {
        BigDecimal BMI = getBMI();
        if (BMI.compareTo(BigDecimal.ZERO) == 0) return "Unknown";
        if (BMI.compareTo(BigDecimal.valueOf(16)) < 0) return "Severe Thinness";
        if (BMI.compareTo(BigDecimal.valueOf(17)) < 0) return "Moderate Thinness";
        if (BMI.compareTo(BigDecimal.valueOf(18.5)) < 0) return "Underweight";
        if (BMI.compareTo(BigDecimal.valueOf(25)) < 0) return "Normal";
        if (BMI.compareTo(BigDecimal.valueOf(30)) < 0) return "Overweight";
        if (BMI.compareTo(BigDecimal.valueOf(35)) < 0) return "Obesity Class I";
        if (BMI.compareTo(BigDecimal.valueOf(40)) < 0) return "Obesity Class II";
        return "Obesity Class III";
    }

    @Transient
    public BigDecimal getBMR(char gender, int age) {
        if (weight == null || height == null) return BigDecimal.ZERO;
        BigDecimal result = BigDecimal.ZERO;

        if (gender == 'M') {
            result = weight.multiply(BigDecimal.TEN)
                    .add(height.multiply(BigDecimal.valueOf(6.25)))
                    .subtract(BigDecimal.valueOf(5L * age))
                    .add(BigDecimal.valueOf(5));
        } else if (gender == 'F') {
            result = weight.multiply(BigDecimal.TEN)
                    .add(height.multiply(BigDecimal.valueOf(6.25)))
                    .subtract(BigDecimal.valueOf(5L * age))
                    .subtract(BigDecimal.valueOf(161));
        }

        return result;
    }

    @Transient
    public BigDecimal getTDEE(char gender, int age) {
        BigDecimal factor = activityFactor != null ? activityFactor : BigDecimal.ONE;
        return getBMR(gender, age).multiply(factor);
    }

    // ==========================================
    // Getters and Setters
    // ==========================================

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public LocalDate getMeasurementDate() { return measurementDate; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getBodyFatPercentage() { return bodyFatPercentage; }
    public void setBodyFatPercentage(BigDecimal bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; }

    public BigDecimal getActivityFactor() { return activityFactor; }
    public void setActivityFactor(BigDecimal activityFactor) { this.activityFactor = activityFactor; }

    public BigDecimal getSmm() { return smm; }
    public void setSmm(BigDecimal smm) { this.smm = smm; }

    public BigDecimal getMuscleMass() { return muscleMass; }
    public void setMuscleMass(BigDecimal muscleMass) { this.muscleMass = muscleMass; }

    public String getPhysicalActivity() { return physicalActivity; }
    public void setPhysicalActivity(String physicalActivity) { this.physicalActivity = physicalActivity; }

    public BigDecimal getArm_C() { return arm_C; }
    public void setArm_C(BigDecimal arm_C) { this.arm_C = arm_C; }

    public BigDecimal getChest_C() { return chest_C; }
    public void setChest_C(BigDecimal chest_C) { this.chest_C = chest_C; }

    public BigDecimal getWaist_C() { return waist_C; }
    public void setWaist_C(BigDecimal waist_C) { this.waist_C = waist_C; }

    public BigDecimal getAbdominal_C() { return abdominal_C; }
    public void setAbdominal_C(BigDecimal abdominal_C) { this.abdominal_C = abdominal_C; }

    public BigDecimal getHip_C() { return hip_C; }
    public void setHip_C(BigDecimal hip_C) { this.hip_C = hip_C; }

    public BigDecimal getMidThigh_C() { return midThigh_C; }
    public void setMidThigh_C(BigDecimal midThigh_C) { this.midThigh_C = midThigh_C; }

    public BigDecimal getCalf_C() { return calf_C; }
    public void setCalf_C(BigDecimal calf_C) { this.calf_C = calf_C; }
}