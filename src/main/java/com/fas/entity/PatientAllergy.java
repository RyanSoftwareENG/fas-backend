package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Allergic_Client")
@AttributeOverride(name = "contraindicated", column = @Column(name = "Allergies_Food", length = 200))
public class PatientAllergy extends HealthInfo {

    @EmbeddedId
    private PatientAllergyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "Client_ID")
    @JsonIgnore
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("allergyId")
    @JoinColumn(name = "Client_Allergy_ID")
    private Allergy allergy;

    public PatientAllergy() {
    }

    public PatientAllergy(Client client,
                          Allergy allergy,
                          String status,
                          String severity,
                          String notes,
                          String contraindicated) {

        super(status, severity, notes, contraindicated);

        this.client = client;
        this.allergy = allergy;

        this.id = new PatientAllergyId(
                client.getClientID(),
                allergy.getId()
        );
    }

    public PatientAllergyId getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Allergy getAllergy() {
        return allergy;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setAllergy(Allergy allergy) {
        this.allergy = allergy;
    }
}