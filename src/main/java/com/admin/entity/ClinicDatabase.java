package com.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "CLINIC_DATABASE",
        schema = "FAS_MANAGEMENT",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CLINIC_DATABASE_CLINIC",
                        columnNames = "CLINIC_ID"
                ),
                @UniqueConstraint(
                        name = "UK_CLINIC_DATABASE_SCHEMA",
                        columnNames = "SCHEMA_NAME"
                )
        }
)
public class ClinicDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATABASE_ID")
    private Long databaseId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CLINIC_ID",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "FK_CLINIC_DATABASE_CLINIC"
            )
    )
    private Clinic clinic;

    @Column(
            name = "DATABASE_NAME",
            nullable = false,
            length = 128
    )
    private String databaseName;

    @Column(
            name = "HOST",
            nullable = false,
            length = 255
    )
    private String host;

    @Column(
            name = "PORT",
            nullable = false
    )
    private Integer port;

    @Column(
            name = "SERVICE_NAME",
            nullable = false,
            length = 128
    )
    private String serviceName;

    @Column(
            name = "SCHEMA_NAME",
            nullable = false,
            unique = true,
            length = 128
    )
    private String schemaName;

    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "CREATED_AT",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "UPDATED_AT",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public ClinicDatabase() {
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}