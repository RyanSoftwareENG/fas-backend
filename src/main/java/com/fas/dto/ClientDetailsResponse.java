package com.fas.dto;

import com.fas.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ClientDetailsResponse {

    private Long clientID;

    private String firstName;
    private String lastName;

    private Character gender;

    private LocalDate birthDate;

    private String contactNumber;

    private LocalDateTime uploadDate;
    private LocalDateTime modificationDate;

    private int age;

    private HealthData healthData;

    private List<PatientAllergy> allergies;

    private List<PatientChronicDisease> chronicDiseases;

    private LifeStyleInformation lifeStyleInformation;

    private List<Session> sessions;

    public ClientDetailsResponse() {
    }

    // =====================================================
    // Getters & Setters
    // =====================================================

    public Long getClientID() {
        return clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(
            LocalDateTime modificationDate
    ) {
        this.modificationDate = modificationDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public HealthData getHealthData() {
        return healthData;
    }

    public void setHealthData(
            HealthData healthData
    ) {
        this.healthData = healthData;
    }

    public List<PatientAllergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(
            List<PatientAllergy> allergies
    ) {
        this.allergies = allergies;
    }

    public List<PatientChronicDisease> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(
            List<PatientChronicDisease> chronicDiseases
    ) {
        this.chronicDiseases = chronicDiseases;
    }

    public LifeStyleInformation getLifeStyleInformation() {
        return lifeStyleInformation;
    }

    public void setLifeStyleInformation(
            LifeStyleInformation lifeStyleInformation
    ) {
        this.lifeStyleInformation =
                lifeStyleInformation;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(
            List<Session> sessions
    ) {
        this.sessions = sessions;
    }
}