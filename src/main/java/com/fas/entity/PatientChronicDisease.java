package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Client_Chronic_Disease")
@AttributeOverride(name = "contraindicated", column = @Column(name = "Contraindicated_Food", length = 500))
public class PatientChronicDisease extends HealthInfo {

    @EmbeddedId
    private PatientChronicDiseaseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "Client_ID")
    @JsonIgnore
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diseaseId")
    @JoinColumn(name = "Chronic_Diseases_ID")
    private ChronicDisease chronicDisease;


    public PatientChronicDisease() {
    }

    public PatientChronicDisease(Client client,
                                 ChronicDisease chronicDisease,
                                 String status,
                                 String severity,
                                 String notes,
                                 String contraindicated) {

        super(status, severity, notes, contraindicated);

        this.client = client;
        this.chronicDisease = chronicDisease;

        this.id = new PatientChronicDiseaseId(
                client.getClientID(),
                chronicDisease.getId()
        );
    }

    public PatientChronicDiseaseId getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public ChronicDisease getChronicDisease() {
        return chronicDisease;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setChronicDisease(
            ChronicDisease chronicDisease) {
        this.chronicDisease = chronicDisease;
    }
}