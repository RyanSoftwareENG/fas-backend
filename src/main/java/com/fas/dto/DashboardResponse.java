package com.fas.dto;

import java.math.BigDecimal;

public class DashboardResponse {

    private long totalClients;
    private long newClients;
    private long totalSessions;
    private long activePlans;
    private BigDecimal periodRevenue;

    private double clientsGrowth;
    private double sessionsGrowth;
    private double plansGrowth;

    private long completedPlans;
    private long cancelledPlans;

    private double averageSessionDuration;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalClients,
            long newClients,
            long totalSessions,
            long activePlans,
            BigDecimal periodRevenue,
            double clientsGrowth,
            double sessionsGrowth,
            double plansGrowth,
            long completedPlans,
            long cancelledPlans,
            double averageSessionDuration
    ) {
        this.totalClients = totalClients;
        this.newClients = newClients;
        this.totalSessions = totalSessions;
        this.activePlans = activePlans;
        this.periodRevenue = periodRevenue;
        this.clientsGrowth = clientsGrowth;
        this.sessionsGrowth = sessionsGrowth;
        this.plansGrowth = plansGrowth;
        this.completedPlans = completedPlans;
        this.cancelledPlans = cancelledPlans;
        this.averageSessionDuration = averageSessionDuration;
    }

    public long getTotalClients() {
        return totalClients;
    }

    public long getNewClients() {
        return newClients;
    }

    public long getTotalSessions() {
        return totalSessions;
    }

    public long getActivePlans() {
        return activePlans;
    }

    public BigDecimal getPeriodRevenue() {
        return periodRevenue;
    }

    public double getClientsGrowth() {
        return clientsGrowth;
    }

    public double getSessionsGrowth() {
        return sessionsGrowth;
    }

    public double getPlansGrowth() {
        return plansGrowth;
    }

    public long getCompletedPlans() {
        return completedPlans;
    }

    public long getCancelledPlans() {
        return cancelledPlans;
    }

    public double getAverageSessionDuration() {
        return averageSessionDuration;
    }

    public void setTotalClients(long totalClients) {
        this.totalClients = totalClients;
    }

    public void setNewClients(long newClients) {
        this.newClients = newClients;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public void setActivePlans(long activePlans) {
        this.activePlans = activePlans;
    }

    public void setPeriodRevenue(BigDecimal periodRevenue) {
        this.periodRevenue = periodRevenue;
    }

    public void setClientsGrowth(double clientsGrowth) {
        this.clientsGrowth = clientsGrowth;
    }

    public void setSessionsGrowth(double sessionsGrowth) {
        this.sessionsGrowth = sessionsGrowth;
    }

    public void setPlansGrowth(double plansGrowth) {
        this.plansGrowth = plansGrowth;
    }

    public void setCompletedPlans(long completedPlans) {
        this.completedPlans = completedPlans;
    }

    public void setCancelledPlans(long cancelledPlans) {
        this.cancelledPlans = cancelledPlans;
    }

    public void setAverageSessionDuration(double averageSessionDuration) {
        this.averageSessionDuration = averageSessionDuration;
    }
}