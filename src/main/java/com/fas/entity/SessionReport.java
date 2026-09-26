package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Session_Report") // 1. تعديل اسم الجدول
public class SessionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Report_ID") // 2. ربط اسم المفتاح الأساسي
    private Long reportId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "Session_ID", // 3. تعديل اسم المفتاح الأجنبي
            nullable = false
    )
    private Session session;

    @Lob
    @Column(name = "Diagnosis")
    private String diagnosis;

    @Lob
    @Column(name = "Assessment")
    private String assessment;

    @Lob
    @Column(name = "Session_Results")
    private String sessionResults;

    @Lob
    @Column(name = "Recommendations")
    private String recommendations;

    @Lob
    @Column(name = "Next_Goals")
    private String nextGoals;

    @Lob
    @Column(name = "Notes")
    private String notes;

    @Column(name = "Next_Appointment")
    private LocalDate nextAppointment;

    @Column(name = "Commitment_Level", length = 50) // حددت طولاً افتراضياً، يمكنك تغييره
    private String commitmentLevel;

    @Column(name = "Nutritionist_Name", length = 100)
    private String nutritionistName;

    // Constructors
    public SessionReport(){}

    // Getters and Setters

    public Session getSession(){
        return session;
    }

    public void setSession(Session session){
        this.session = session;
    }

    public Long getReportId(){
        return reportId;
    }

    public void setReportId(Long reportId){
        this.reportId = reportId;
    }

    public String getDiagnosis(){
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis){
        this.diagnosis = diagnosis;
    }

    public String getAssessment(){
        return assessment;
    }

    public void setAssessment(String assessment){
        this.assessment = assessment;
    }

    public String getSessionResults(){
        return sessionResults;
    }

    public void setSessionResults(String sessionResults){
        this.sessionResults = sessionResults;
    }

    public String getRecommendations(){
        return recommendations;
    }

    public void setRecommendations(String recommendations){
        this.recommendations = recommendations;
    }

    public String getNextGoals(){
        return nextGoals;
    }

    public void setNextGoals(String nextGoals){
        this.nextGoals = nextGoals;
    }

    public String getNotes(){
        return notes;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    public LocalDate getNextAppointment(){
        return nextAppointment;
    }

    public void setNextAppointment(LocalDate nextAppointment){
        this.nextAppointment = nextAppointment;
    }

    public String getCommitmentLevel(){
        return commitmentLevel;
    }

    public void setCommitmentLevel(String commitmentLevel){
        this.commitmentLevel = commitmentLevel;
    }

    public String getNutritionistName(){
        return nutritionistName;
    }

    public void setNutritionistName(String nutritionistName){
        this.nutritionistName = nutritionistName;
    }
}