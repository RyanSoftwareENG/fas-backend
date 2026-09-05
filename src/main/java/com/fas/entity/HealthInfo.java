package com.fas.entity;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class HealthInfo {

    @Column(name = "Status", length = 50)
    protected String status;

    @Column(name = "Severity", length = 50)
    protected String severity;

    @Column(name = "Notes", length = 500)
    private String notes;

    protected String contraindicated;

    public HealthInfo() {
    }

    public HealthInfo(
            String status,
            String severity,
            String notes,
            String contraindicated) {

        this.status = status;
        this.severity = severity;
        this.notes = notes;
        this.contraindicated = contraindicated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getContraindicated() {
        return contraindicated;
    }

    public void setContraindicated(String contraindicated) {
        this.contraindicated = contraindicated;
    }
}