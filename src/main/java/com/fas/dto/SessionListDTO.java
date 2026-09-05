package com.fas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SessionListDTO {

    // =========================
    // Session Data
    // =========================

    private Long id;
    private LocalDateTime uploadTime;
    private String duration;
    private BigDecimal price;
    private String notes;

    // =========================
    // Client Basic Data
    // =========================

    private Long clientID;
    private String firstName;
    private String lastName;
    private Character gender;
    private LocalDate birthDate;
    private String contactNumber;
    private LocalDateTime uploadDate;
    private LocalDateTime modificationDate;

    public SessionListDTO(
            Long id,
            LocalDateTime uploadTime,
            String duration,
            BigDecimal price,
            String notes,

            Long clientID,
            String firstName,
            String lastName,
            Character gender,
            LocalDate birthDate,
            String contactNumber,
            LocalDateTime uploadDate,
            LocalDateTime modificationDate) {

        // Session
        this.id = id;
        this.uploadTime = uploadTime;
        this.duration = duration;
        this.price = price;
        this.notes = notes;

        // Client
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.contactNumber = contactNumber;
        this.uploadDate = uploadDate;
        this.modificationDate = modificationDate;
    }

    // =========================
    // Session Getters
    // =========================

    public Long getId() {
        return id;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public String getDuration() {
        return duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getNotes() {
        return notes;
    }

    // =========================
    // Client Getters
    // =========================

    public Long getClientID() {
        return clientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Character getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    // =========================
    // Full Name
    // =========================

    public String getClientName() {
        return (firstName != null ? firstName : "")
                + " "
                + (lastName != null ? lastName : "");
    }
}