package com.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "SOFTWARE_VERSION",
        schema = "FAS_MANAGEMENT"
)
public class SoftwareVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VERSION_ID", nullable = false)
    private Long versionId;

    @Column(
            name = "APPLICATION_NAME",
            nullable = false,
            length = 50
    )
    private String applicationName;

    @Column(
            name = "VERSION_NUMBER",
            nullable = false,
            length = 50
    )
    private String versionNumber;

    @Column(
            name = "MINIMUM_VERSION",
            length = 50
    )
    private String minimumVersion;

    @Column(
            name = "RELEASE_DATE",
            nullable = false
    )
    private LocalDate releaseDate;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "MANDATORY",
            nullable = false
    )
    private Boolean mandatory;

    @Lob
    @Column(name = "RELEASE_NOTES")
    private String releaseNotes;

    public SoftwareVersion() {
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getMinimumVersion() {
        return minimumVersion;
    }

    public void setMinimumVersion(String minimumVersion) {
        this.minimumVersion = minimumVersion;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }
}