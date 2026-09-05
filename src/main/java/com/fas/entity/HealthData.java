package com.fas.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "Health_Data")
public class HealthData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Health_Data_ID")
    private Long id;


    @OneToOne
    @JoinColumn(name = "Client_ID")
    @JsonIgnore
    private Client client;

    @Column(name = "Medical_History", length = 1000)
    private String medicalHistory;

    @Column(name = "Family_Medical_History", length = 1000)
    private String familyMedicalHistory;

    @Column(name = "Notes", length = 1000)
    private String notes;

    @Column(name = "Current_Medication" ,length = 500)
    private String currentMedication;




    public HealthData(){}



    private HealthData(HealthDataBuilder builder){

        this.currentMedication = builder.currentMedication;
        this.medicalHistory = builder.medicalHistory;
        this.familyMedicalHistory = builder.familyMedicalHistory;
        this.notes = builder.notes;

    }



    public static class HealthDataBuilder {

        private String currentMedication;
        private String medicalHistory;
        private String familyMedicalHistory;
        private String notes;


        public HealthDataBuilder currentMedication(String value){
            this.currentMedication = value;
            return this;
        }


        public HealthDataBuilder medicalHistory(String value){
            this.medicalHistory = value;
            return this;
        }


        public HealthDataBuilder familyMedicalHistory(String value){
            this.familyMedicalHistory = value;
            return this;
        }


        public HealthDataBuilder notes(String value){
            this.notes = value;
            return this;
        }


        public HealthData build(){
            return new HealthData(this);
        }
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId(){
        return id;
    }


    public Client getClient(){
        return client;
    }


    public void setClient(Client client){
        this.client = client;
    }


    public String getCurrentMedication(){
        return currentMedication;
    }


    public void setCurrentMedication(String currentMedication){
        this.currentMedication = currentMedication;
    }


    public String getMedicalHistory(){
        return medicalHistory;
    }


    public void setMedicalHistory(String medicalHistory){
        this.medicalHistory = medicalHistory;
    }


    public String getFamilyMedicalHistory(){
        return familyMedicalHistory;
    }


    public void setFamilyMedicalHistory(String familyMedicalHistory){
        this.familyMedicalHistory = familyMedicalHistory;
    }


    public String getNotes(){
        return notes;
    }


    public void setNotes(String notes){
        this.notes = notes;
    }
}