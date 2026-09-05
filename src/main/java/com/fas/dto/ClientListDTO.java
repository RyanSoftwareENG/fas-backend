package com.fas.dto;

import com.fas.entity.Client;

import java.time.LocalDate;

public class ClientListDTO {

    private Long clientID;
    private String firstName;
    private String lastName;
    private Character gender;
    private LocalDate birthDate;
    private String contactNumber;

    public ClientListDTO() {
    }

    public ClientListDTO(
            Long clientID,
            String firstName,
            String lastName,
            Character gender,
            LocalDate birthDate,
            String contactNumber) {

        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.contactNumber = contactNumber;
    }

    public ClientListDTO(Client client) {
        this.clientID = client.getClientID();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.gender = client.getGender();
        this.birthDate = client.getBirthDate();
        this.contactNumber = client.getContactNumber();
    }

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
}