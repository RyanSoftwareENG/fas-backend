package com.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminDashboardResponse {

    // =====================================================
    // الإحصائيات الأساسية
    // =====================================================

    private long totalClinics;
    private long activeClinics;
    private long suspendedClinics;
    private long closedClinics;

    private long totalUsers;
    private long activeUsers;
    private long suspendedUsers;
    private long disabledUsers;

    private long totalPlans;
    private long activePlans;
    private long disabledPlans;

    private long totalSubscriptions;
    private long pendingSubscriptions;
    private long activeSubscriptions;
    private long expiredSubscriptions;
    private long suspendedSubscriptions;
    private long cancelledSubscriptions;

    private long totalDevices;
    private long activeDevices;
    private long blockedDevices;
    private long revokedDevices;

    private long activeUserSessions;
    private long activeAdminSessions;

    private long totalAdminUsers;
    private long activeAdminUsers;

    private long totalRoles;
    private long totalPermissions;

    private List<ActivityItem> recentActivities;
    private List<ActivationItem> recentActivations;

    public AdminDashboardResponse() {
    }

    // =====================================================
    // Getters / Setters
    // =====================================================

    public long getTotalClinics() {
        return totalClinics;
    }

    public void setTotalClinics(long totalClinics) {
        this.totalClinics = totalClinics;
    }

    public long getActiveClinics() {
        return activeClinics;
    }

    public void setActiveClinics(long activeClinics) {
        this.activeClinics = activeClinics;
    }

    public long getSuspendedClinics() {
        return suspendedClinics;
    }

    public void setSuspendedClinics(long suspendedClinics) {
        this.suspendedClinics = suspendedClinics;
    }

    public long getClosedClinics() {
        return closedClinics;
    }

    public void setClosedClinics(long closedClinics) {
        this.closedClinics = closedClinics;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getSuspendedUsers() {
        return suspendedUsers;
    }

    public void setSuspendedUsers(long suspendedUsers) {
        this.suspendedUsers = suspendedUsers;
    }

    public long getDisabledUsers() {
        return disabledUsers;
    }

    public void setDisabledUsers(long disabledUsers) {
        this.disabledUsers = disabledUsers;
    }

    public long getTotalPlans() {
        return totalPlans;
    }

    public void setTotalPlans(long totalPlans) {
        this.totalPlans = totalPlans;
    }

    public long getActivePlans() {
        return activePlans;
    }

    public void setActivePlans(long activePlans) {
        this.activePlans = activePlans;
    }

    public long getDisabledPlans() {
        return disabledPlans;
    }

    public void setDisabledPlans(long disabledPlans) {
        this.disabledPlans = disabledPlans;
    }

    public long getTotalSubscriptions() {
        return totalSubscriptions;
    }

    public void setTotalSubscriptions(long totalSubscriptions) {
        this.totalSubscriptions = totalSubscriptions;
    }

    public long getPendingSubscriptions() {
        return pendingSubscriptions;
    }

    public void setPendingSubscriptions(long pendingSubscriptions) {
        this.pendingSubscriptions = pendingSubscriptions;
    }

    public long getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(long activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    public long getExpiredSubscriptions() {
        return expiredSubscriptions;
    }

    public void setExpiredSubscriptions(long expiredSubscriptions) {
        this.expiredSubscriptions = expiredSubscriptions;
    }

    public long getSuspendedSubscriptions() {
        return suspendedSubscriptions;
    }

    public void setSuspendedSubscriptions(long suspendedSubscriptions) {
        this.suspendedSubscriptions = suspendedSubscriptions;
    }

    public long getCancelledSubscriptions() {
        return cancelledSubscriptions;
    }

    public void setCancelledSubscriptions(long cancelledSubscriptions) {
        this.cancelledSubscriptions = cancelledSubscriptions;
    }

    public long getTotalDevices() {
        return totalDevices;
    }

    public void setTotalDevices(long totalDevices) {
        this.totalDevices = totalDevices;
    }

    public long getActiveDevices() {
        return activeDevices;
    }

    public void setActiveDevices(long activeDevices) {
        this.activeDevices = activeDevices;
    }

    public long getBlockedDevices() {
        return blockedDevices;
    }

    public void setBlockedDevices(long blockedDevices) {
        this.blockedDevices = blockedDevices;
    }

    public long getRevokedDevices() {
        return revokedDevices;
    }

    public void setRevokedDevices(long revokedDevices) {
        this.revokedDevices = revokedDevices;
    }

    public long getActiveUserSessions() {
        return activeUserSessions;
    }

    public void setActiveUserSessions(long activeUserSessions) {
        this.activeUserSessions = activeUserSessions;
    }

    public long getActiveAdminSessions() {
        return activeAdminSessions;
    }

    public void setActiveAdminSessions(long activeAdminSessions) {
        this.activeAdminSessions = activeAdminSessions;
    }

    public long getTotalAdminUsers() {
        return totalAdminUsers;
    }

    public void setTotalAdminUsers(long totalAdminUsers) {
        this.totalAdminUsers = totalAdminUsers;
    }

    public long getActiveAdminUsers() {
        return activeAdminUsers;
    }

    public void setActiveAdminUsers(long activeAdminUsers) {
        this.activeAdminUsers = activeAdminUsers;
    }

    public long getTotalRoles() {
        return totalRoles;
    }

    public void setTotalRoles(long totalRoles) {
        this.totalRoles = totalRoles;
    }

    public long getTotalPermissions() {
        return totalPermissions;
    }

    public void setTotalPermissions(long totalPermissions) {
        this.totalPermissions = totalPermissions;
    }

    public List<ActivityItem> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(
            List<ActivityItem> recentActivities
    ) {
        this.recentActivities = recentActivities;
    }

    public List<ActivationItem> getRecentActivations() {
        return recentActivations;
    }

    public void setRecentActivations(
            List<ActivationItem> recentActivations
    ) {
        this.recentActivations = recentActivations;
    }

    // =====================================================
    // Activity
    // =====================================================

    public static class ActivityItem {

        private Long logId;
        private Long adminUserId;
        private String action;
        private String entityName;
        private Long entityId;
        private String details;
        private LocalDateTime createdAt;

        public ActivityItem() {
        }

        public ActivityItem(
                Long logId,
                Long adminUserId,
                String action,
                String entityName,
                Long entityId,
                String details,
                LocalDateTime createdAt
        ) {
            this.logId = logId;
            this.adminUserId = adminUserId;
            this.action = action;
            this.entityName = entityName;
            this.entityId = entityId;
            this.details = details;
            this.createdAt = createdAt;
        }

        public Long getLogId() {
            return logId;
        }

        public Long getAdminUserId() {
            return adminUserId;
        }

        public String getAction() {
            return action;
        }

        public String getEntityName() {
            return entityName;
        }

        public Long getEntityId() {
            return entityId;
        }

        public String getDetails() {
            return details;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    // =====================================================
    // Subscription Activation
    // =====================================================

    public static class ActivationItem {

        private Long activationId;
        private Long subscriptionId;
        private Long adminUserId;
        private String previousStatus;
        private String newStatus;
        private String notes;
        private LocalDateTime activationDate;

        public ActivationItem() {
        }

        public ActivationItem(
                Long activationId,
                Long subscriptionId,
                Long adminUserId,
                String previousStatus,
                String newStatus,
                String notes,
                LocalDateTime activationDate
        ) {
            this.activationId = activationId;
            this.subscriptionId = subscriptionId;
            this.adminUserId = adminUserId;
            this.previousStatus = previousStatus;
            this.newStatus = newStatus;
            this.notes = notes;
            this.activationDate = activationDate;
        }

        public Long getActivationId() {
            return activationId;
        }

        public Long getSubscriptionId() {
            return subscriptionId;
        }

        public Long getAdminUserId() {
            return adminUserId;
        }

        public String getPreviousStatus() {
            return previousStatus;
        }

        public String getNewStatus() {
            return newStatus;
        }

        public String getNotes() {
            return notes;
        }

        public LocalDateTime getActivationDate() {
            return activationDate;
        }
    }
}