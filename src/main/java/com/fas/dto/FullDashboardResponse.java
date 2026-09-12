package com.fas.dto;

import java.util.List;

public class FullDashboardResponse {

    private DashboardResponse overview;

    private List<SessionTrendResponse> sessionsTrend;
    private List<RevenueTrendResponse> revenueTrend;
    private List<PlanStatusResponse> planStatuses;
    private List<ChronicDiseaseReportResponse> chronicDiseases;
    private List<AllergyReportResponse> allergies;
    private List<RecentSessionResponse> recentSessions;
    private List<ReportAlertResponse> alerts;

    public FullDashboardResponse() {
    }

    public FullDashboardResponse(
            DashboardResponse overview,
            List<SessionTrendResponse> sessionsTrend,
            List<RevenueTrendResponse> revenueTrend,
            List<PlanStatusResponse> planStatuses,
            List<ChronicDiseaseReportResponse> chronicDiseases,
            List<AllergyReportResponse> allergies,
            List<RecentSessionResponse> recentSessions,
            List<ReportAlertResponse> alerts
    ) {
        this.overview = overview;
        this.sessionsTrend = sessionsTrend;
        this.revenueTrend = revenueTrend;
        this.planStatuses = planStatuses;
        this.chronicDiseases = chronicDiseases;
        this.allergies = allergies;
        this.recentSessions = recentSessions;
        this.alerts = alerts;
    }

    public DashboardResponse getOverview() {
        return overview;
    }

    public void setOverview(DashboardResponse overview) {
        this.overview = overview;
    }

    public List<SessionTrendResponse> getSessionsTrend() {
        return sessionsTrend;
    }

    public void setSessionsTrend(
            List<SessionTrendResponse> sessionsTrend
    ) {
        this.sessionsTrend = sessionsTrend;
    }

    public List<RevenueTrendResponse> getRevenueTrend() {
        return revenueTrend;
    }

    public void setRevenueTrend(
            List<RevenueTrendResponse> revenueTrend
    ) {
        this.revenueTrend = revenueTrend;
    }

    public List<PlanStatusResponse> getPlanStatuses() {
        return planStatuses;
    }

    public void setPlanStatuses(
            List<PlanStatusResponse> planStatuses
    ) {
        this.planStatuses = planStatuses;
    }

    public List<ChronicDiseaseReportResponse> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(
            List<ChronicDiseaseReportResponse> chronicDiseases
    ) {
        this.chronicDiseases = chronicDiseases;
    }

    public List<AllergyReportResponse> getAllergies() {
        return allergies;
    }

    public void setAllergies(
            List<AllergyReportResponse> allergies
    ) {
        this.allergies = allergies;
    }

    public List<RecentSessionResponse> getRecentSessions() {
        return recentSessions;
    }

    public void setRecentSessions(
            List<RecentSessionResponse> recentSessions
    ) {
        this.recentSessions = recentSessions;
    }

    public List<ReportAlertResponse> getAlerts() {
        return alerts;
    }

    public void setAlerts(
            List<ReportAlertResponse> alerts
    ) {
        this.alerts = alerts;
    }
}