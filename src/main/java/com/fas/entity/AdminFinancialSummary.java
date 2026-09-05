package com.fas.entity;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminFinancialSummary {
    private LocalDate startDate;
    private LocalDate endDate;

    private int totalSessions;
    private double totalRevenue;
    private int newClients;

    // مفتاح: مستوى الالتزام (مثل "Moderate")، قيمة: عدد الجلسات بهذا المستوى
    private Map<String, Integer> commitmentDistribution = new LinkedHashMap<>();

    public AdminFinancialSummary() {}

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getNewClients() {
        return newClients;
    }

    public void setNewClients(int newClients) {
        this.newClients = newClients;
    }

    public Map<String, Integer> getCommitmentDistribution() {
        return commitmentDistribution;
    }

    public void setCommitmentDistribution(Map<String, Integer> commitmentDistribution) {
        this.commitmentDistribution = commitmentDistribution;
    }
}