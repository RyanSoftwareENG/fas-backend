package com.fas.service;

import com.fas.entity.Session;
import com.fas.entity.SessionReport;
import com.fas.repository.SessionReportRepository;
import com.fas.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SessionReportService {

    private final SessionReportRepository reportRepository;
    private final SessionRepository sessionRepository;

    public SessionReportService(
            SessionReportRepository reportRepository,
            SessionRepository sessionRepository) {

        this.reportRepository = reportRepository;
        this.sessionRepository = sessionRepository;
    }

    // =====================================================
    // جلب جميع التقارير
    // =====================================================

    @Transactional(readOnly = true)
    public List<SessionReport> getAllReports() {

        return reportRepository.findAll();
    }

    // =====================================================
    // جلب تقرير بواسطة Report ID
    // =====================================================

    @Transactional(readOnly = true)
    public SessionReport getReportById(Long reportId) {

        return reportRepository.findById(reportId)
                .orElse(null);
    }

    // =====================================================
    // جلب التقرير بواسطة Session ID
    // =====================================================

    @Transactional(readOnly = true)
    public SessionReport getReportBySessionId(Long sessionId) {

        return reportRepository
                .findBySessionId(sessionId)
                .orElse(null);
    }

    // =====================================================
    // حفظ تقرير جديد
    // =====================================================

    public SessionReport saveReport(SessionReport report) {

        if (report == null) {
            throw new IllegalArgumentException(
                    "بيانات التقرير لا يمكن أن تكون فارغة"
            );
        }

        if (report.getSession() == null ||
                report.getSession().getId() == null) {

            throw new IllegalArgumentException(
                    "يجب تحديد Session ID للتقرير"
            );
        }

        Long sessionId =
                report.getSession().getId();

        Session session =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الجلسة غير موجودة بالرقم: "
                                                + sessionId
                                )
                        );

        /*
         * نربط التقرير بالـ Session الموجودة فعلياً
         * في قاعدة البيانات.
         */
        report.setSession(session);

        return reportRepository.save(report);
    }

    // =====================================================
    // تحديث التقرير
    // =====================================================

    public SessionReport updateReport(
            Long reportId,
            SessionReport newReport) {

        SessionReport existingReport =
                reportRepository.findById(reportId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "التقرير غير موجود بالرقم: "
                                                + reportId
                                )
                        );

        existingReport.setDiagnosis(
                newReport.getDiagnosis()
        );

        existingReport.setAssessment(
                newReport.getAssessment()
        );

        existingReport.setSessionResults(
                newReport.getSessionResults()
        );

        existingReport.setRecommendations(
                newReport.getRecommendations()
        );

        existingReport.setNextGoals(
                newReport.getNextGoals()
        );

        existingReport.setNotes(
                newReport.getNotes()
        );

        existingReport.setNextAppointment(
                newReport.getNextAppointment()
        );

        existingReport.setCommitmentLevel(
                newReport.getCommitmentLevel()
        );

        existingReport.setNutritionistName(
                newReport.getNutritionistName()
        );

        /*
         * إذا أرسل العميل Session جديدة،
         * نتحقق منها قبل تغيير العلاقة.
         */
        if (newReport.getSession() != null &&
                newReport.getSession().getId() != null) {

            Long sessionId =
                    newReport.getSession().getId();

            Session session =
                    sessionRepository.findById(sessionId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "الجلسة غير موجودة بالرقم: "
                                                    + sessionId
                                    )
                            );

            existingReport.setSession(session);
        }

        return reportRepository.save(existingReport);
    }

    // =====================================================
    // حذف التقرير
    // =====================================================

    public void deleteReport(Long reportId) {

        if (!reportRepository.existsById(reportId)) {

            throw new RuntimeException(
                    "التقرير غير موجود بالرقم: "
                            + reportId
            );
        }

        reportRepository.deleteById(reportId);
    }
}