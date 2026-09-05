package com.fas.dto;

import com.fas.entity.HealthData;
import com.fas.entity.LifeStyleInformation;

import java.time.LocalDate;

public class ClientRequest {

    private String firstName;

    private String lastName;

    private Character gender;

    private LocalDate birthDate;

    private String contactNumber;

    private HealthData healthData;

    private LifeStyleInformation lifeStyleInformation;


    public ClientRequest() {
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


    public HealthData getHealthData() {
        return healthData;
    }


    public void setHealthData(HealthData healthData) {
        this.healthData = healthData;
    }


    public LifeStyleInformation getLifeStyleInformation() {
        return lifeStyleInformation;
    }


    public void setLifeStyleInformation(LifeStyleInformation lifeStyleInformation) {
        this.lifeStyleInformation = lifeStyleInformation;
    }
}