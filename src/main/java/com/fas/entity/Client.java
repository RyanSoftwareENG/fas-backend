package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Client_ID")
    private Long clientID;


    @Column(name = "First_Name", nullable = false, length = 30)
    private String firstName;


    @Column(name = "Last_Name", nullable = false, length = 20)
    private String lastName;


    @Column(name = "Gender", nullable = false, length = 1)
    private Character gender;


    @Column(name = "Birth_Date", nullable = false)
    private LocalDate birthDate;


    @Column(name = "Contact_Number", length = 20)
    private String contactNumber;


    @CreationTimestamp
    @Column(name = "Creation_Date",
            nullable = false,
            updatable = false)
    private LocalDateTime uploadDate;


    @UpdateTimestamp
    @Column(name = "Update_Date")
    private LocalDateTime modificationDate;


// =========================
// Health Data
// =========================

    @OneToOne(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private HealthData healthData;


// =========================
// Allergies
// =========================

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PatientAllergy> allergies = new ArrayList<>();


// =========================
// Chronic Diseases
// =========================

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PatientChronicDisease> chronicDiseases =
            new ArrayList<>();


// =========================
// Lifestyle
// =========================

    @OneToOne(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private LifeStyleInformation lifeStyleInformation;


// =========================
// Sessions
// =========================

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private List<Session> sessions = new ArrayList<>();
    // Hibernate ظٹط­طھط§ط¬ Constructor ظپط§ط±ط؛
    public Client() {
    }

    public Client(String firstName, String lastName, Character gender, LocalDate birthDate) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    // =========================
    // Getters & Setters
    // =========================


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


    public LocalDateTime getModificationDate() {
        return modificationDate;
    }


    public HealthData getHealthData() {
        return healthData;
    }


    public void setHealthData(HealthData healthData) {

        this.healthData = healthData;

        if (healthData != null) {
            healthData.setClient(this);
        }
    }


    public LifeStyleInformation getLifeStyleInformation() {
        return lifeStyleInformation;
    }


    public void setLifeStyleInformation(
            LifeStyleInformation lifeStyleInformation) {

        this.lifeStyleInformation = lifeStyleInformation;

        if (lifeStyleInformation != null) {
            lifeStyleInformation.setClient(this);
        }
    }



    public List<Session> getSessions() {
        return sessions;
    }



    public void setSessions(List<Session> sessions) {

        this.sessions.clear();

        if(sessions != null) {

            for(Session session : sessions) {
                addSession(session);
            }
        }
    }



    public void addSession(Session session) {

        if(session != null){

            sessions.add(session);
            session.setClient(this);

        }
    }

    public void removeSession(Session session) {

        if(session != null){

            sessions.remove(session);
            session.setClient(null);

        }
    }

    public void setAllergies(List<PatientAllergy> allergies) {
        this.allergies = allergies;
    }

    public void setChronicDiseases(List<PatientChronicDisease> chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }
// =========================
    // Calculated Fields
    // =========================


    @Transient
    public int getAge() {

        if (birthDate == null) {
            return 0;
        }

        return Period
                .between(birthDate, LocalDate.now())
                .getYears();
    }



    @Transient
    public String getFullName() {

        StringBuilder name = new StringBuilder();

        if(firstName != null)
            name.append(firstName);

        if(lastName != null)
            name.append(" ").append(lastName);

        return name.toString().trim();
    }



    @Transient
    public void setFullName(String fullName) {

        if(fullName == null || fullName.trim().isEmpty()) {

            this.firstName = "Unknown";
            this.lastName = "Unknown";

            return;
        }


        String[] parts = fullName.trim().split("\\s+");


        if(parts.length == 1) {

            this.firstName = parts[0];
            this.lastName = "Unknown";

        }else {

            this.firstName = parts[0];

            StringBuilder last = new StringBuilder();

            for(int i = 1; i < parts.length; i++) {

                last.append(parts[i]);

                if(i < parts.length - 1)
                    last.append(" ");
            }

            this.lastName = last.toString();
        }
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public List<PatientAllergy> getAllergies() {
        return allergies;
    }

    public List<PatientChronicDisease> getChronicDiseases() {
        return chronicDiseases;
    }
}