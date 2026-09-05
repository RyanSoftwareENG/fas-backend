package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Chronic_Disease")
public class ChronicDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // تم التعديل ليطابق SQL بإضافة حرف s
    @Column(name = "Chronic_Diseases_ID")
    private Long id;

    @OneToMany(
            mappedBy = "chronicDisease",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonBackReference
    private List<PatientChronicDisease> patientDiseases = new ArrayList<>();

    // تم التعديل ليطابق الاسم بالكامل في SQL
    @Column(name = "Chronic_Diseases_Name", nullable = false, unique = true, length = 100)
    private String diseaseName;

    public ChronicDisease() {
    }

    public ChronicDisease(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }
}