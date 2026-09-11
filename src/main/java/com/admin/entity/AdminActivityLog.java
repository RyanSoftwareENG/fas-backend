package com.admin.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ADMIN_ACTIVITY_LOG",
        schema = "FAS_MANAGEMENT"
)
public class AdminActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID", nullable = false)
    private Long logId;

    @Column(name = "ADMIN_USER_ID")
    private Long adminUserId;

    @Column(
            name = "ACTION",
            nullable = false,
            length = 100
    )
    private String action;

    @Column(
            name = "ENTITY_NAME",
            length = 100
    )
    private String entityName;

    @Column(name = "ENTITY_ID")
    private Long entityId;

    @Lob
    @Column(name = "DETAILS")
    private String details;

    @Column(
            name = "CREATED_AT",
            nullable = false
    )
    private LocalDateTime createdAt;

    public AdminActivityLog() {
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}