package com.fas.service;

import com.fas.dto.*;
import com.fas.repository.DashboardReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardReportService {

    private final DashboardReportRepository repository;

    public DashboardReportService(
            DashboardReportRepository repository
    ) {
        this.repository = repository;
    }

    // =====================================================
    // Dashboard
    // =====================================================

    public DashboardResponse getDashboard(
            int days
    ) {

        int finalDays =
                normalizeDays(days);

        DashboardResponse response =
                new DashboardResponse();

        // -------------------------------------------------
        // إجمالي المرضى
        // -------------------------------------------------

        response.setTotalClients(
                repository.countClients()
        );

        // -------------------------------------------------
        // المرضى الجدد خلال الفترة
        // -------------------------------------------------

        response.setNewClients(
                repository.countNewClients(
                        finalDays
                )
        );

        // -------------------------------------------------
        // الجلسات خلال الفترة
        // -------------------------------------------------

        response.setTotalSessions(
                repository.countSessions(
                        finalDays
                )
        );

        // -------------------------------------------------
        // الخطط النشطة حاليًا
        // -------------------------------------------------

        response.setActivePlans(
                repository.countActivePlans()
        );

        // -------------------------------------------------
        // الخطط المكتملة خلال الفترة
        // -------------------------------------------------

        response.setCompletedPlans(
                repository.countCompletedPlans(
                        finalDays
                )
        );

        // -------------------------------------------------
        // الخطط الملغاة خلال الفترة
        // -------------------------------------------------

        response.setCancelledPlans(
                repository.countCancelledPlans(
                        finalDays
                )
        );

        // -------------------------------------------------
        // الإيرادات خلال الفترة
        // -------------------------------------------------

        response.setPeriodRevenue(
                repository.calculatePeriodRevenue(
                        finalDays
                )
        );

        // -------------------------------------------------
        // متوسط مدة الجلسة خلال الفترة
        // -------------------------------------------------

        response.setAverageSessionDuration(
                repository.calculateAverageSessionDuration(
                        finalDays
                )
        );

        // -------------------------------------------------
        // النمو
        //
        // لم يتم بناء مقارنة الفترة الحالية
        // بالفترة السابقة حتى الآن.
        // -------------------------------------------------

        response.setClientsGrowth(
                0
        );

        response.setSessionsGrowth(
                0
        );

        response.setPlansGrowth(
                0
        );

        return response;
    }

    // =====================================================
    // Dashboard افتراضي
    // =====================================================

    public DashboardResponse getDashboard() {

        return getDashboard(30);
    }

    // =====================================================
    // Session Trend
    // =====================================================

    public List<SessionTrendResponse> getSessionTrend(
            int days
    ) {

        return repository.getSessionTrend(
                normalizeDays(days)
        );
    }

    // =====================================================
    // Plan Statuses
    // =====================================================

    public List<PlanStatusResponse> getPlanStatuses() {

        return repository.getPlanStatuses();
    }

    // =====================================================
    // Chronic Diseases
    // =====================================================

    public List<ChronicDiseaseReportResponse>
    getChronicDiseaseStatistics() {

        return repository.getChronicDiseaseStatistics();
    }

    // =====================================================
    // Allergies
    // =====================================================

    public List<AllergyReportResponse>
    getAllergyStatistics() {

        return repository.getAllergyStatistics();
    }

    // =====================================================
    // Body Progress
    // =====================================================

    public List<BodyProgressResponse> getBodyProgress(
            Long clientId
    ) {

        validateClientId(clientId);

        return repository.getBodyProgress(
                clientId
        );
    }

    // =====================================================
    // Patient Progress Summary
    // =====================================================

    public PatientProgressSummary getPatientProgressSummary(
            Long clientId
    ) {

        validateClientId(clientId);

        return repository.getPatientProgressSummary(
                clientId
        );
    }

    // =====================================================
    // Recent Sessions
    // =====================================================

    public List<RecentSessionResponse> getRecentSessions(
            int limit
    ) {

        return repository.getRecentSessions(
                normalizeLimit(limit)
        );
    }

    // =====================================================
    // Revenue Trend
    // =====================================================

    public List<RevenueTrendResponse> getRevenueTrend(
            int days
    ) {

        return repository.getRevenueTrend(
                normalizeDays(days)
        );
    }

    // =====================================================
    // Alerts
    // =====================================================

    public List<ReportAlertResponse> getAlerts() {

        return repository.getAlerts();
    }

    // =====================================================
    // Full Dashboard
    // =====================================================

    public FullDashboardResponse getFullDashboard(
            int days,
            int recentLimit
    ) {

        int finalDays =
                normalizeDays(days);

        int finalRecentLimit =
                normalizeLimit(recentLimit);

        DashboardResponse overview =
                getDashboard(finalDays);

        return new FullDashboardResponse(

                // Overview
                overview,

                // اتجاه الجلسات
                getSessionTrend(
                        finalDays
                ),

                // اتجاه الإيرادات
                getRevenueTrend(
                        finalDays
                ),

                // حالة الخطط
                getPlanStatuses(),

                // الأمراض المزمنة
                getChronicDiseaseStatistics(),

                // الحساسية
                getAllergyStatistics(),

                // آخر الجلسات
                getRecentSessions(
                        finalRecentLimit
                ),

                // التنبيهات
                getAlerts()
        );
    }

    // =====================================================
    // Helpers
    // =====================================================

    private int normalizeDays(
            int days
    ) {

        if (days <= 0) {
            return 30;
        }

        return Math.min(
                days,
                365
        );
    }

    private int normalizeLimit(
            int limit
    ) {

        if (limit <= 0) {
            return 10;
        }

        return Math.min(
                limit,
                50
        );
    }

    private void validateClientId(
            Long clientId
    ) {

        if (clientId == null
                || clientId <= 0) {

            throw new IllegalArgumentException(
                    "رقم المريض غير صالح."
            );
        }
    }
}