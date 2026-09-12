package com.fas.repository;

import com.fas.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DashboardReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardReportRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =====================================================
    // إجمالي المرضى
    // =====================================================

    public long countClients() {

        String sql = """
                SELECT COUNT(*)
                FROM CLIENT
                """;

        Long result = jdbcTemplate.queryForObject(
                sql,
                Long.class
        );

        return result != null
                ? result
                : 0L;
    }

    // =====================================================
    // المرضى الجدد خلال الفترة
    // =====================================================

    public long countNewClients(int days) {

        String sql = """
            SELECT COUNT(*)
            FROM CLIENT
            WHERE CREATION_DATE >=
                  TRUNC(SYSDATE) - ?
            """;

        Long result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{days},
                Long.class
        );

        return result != null ? result : 0L;
    }
    // =====================================================
    // الجلسات خلال الفترة
    // =====================================================

    public long countSessions(int days) {

        String sql = """
            SELECT COUNT(*)
            FROM SESSION_RECORD
            WHERE SESSION_DATE >=
                  TRUNC(SYSDATE) - ?
            """;

        Long result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{days},
                Long.class
        );

        return result != null ? result : 0L;
    }

    // =====================================================
    // الخطط النشطة حاليًا
    // =====================================================

    public long countActivePlans() {

        String sql = """
                SELECT COUNT(*)
                FROM NUTRITIONPLAN
                WHERE PLAN_STATUS = 'Active'
                """;

        Long result = jdbcTemplate.queryForObject(
                sql,
                Long.class
        );

        return result != null
                ? result
                : 0L;
    }

    // =====================================================
    // الخطط المكتملة خلال الفترة
    // =====================================================

    public long countCompletedPlans(int days) {

        String sql = """
            SELECT COUNT(*)
            FROM NUTRITIONPLAN
            WHERE PLAN_STATUS = 'Completed'
              AND END_DATE >=
                  TRUNC(SYSDATE) - ?
            """;

        Long result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{days},
                Long.class
        );

        return result != null ? result : 0L;
    }
    // =====================================================
    // الخطط الملغاة خلال الفترة
    // =====================================================

    public long countCancelledPlans(int days) {

        String sql = """
            SELECT COUNT(*)
            FROM NUTRITIONPLAN
            WHERE PLAN_STATUS = 'Cancelled'
              AND NVL(END_DATE, TRUNC(SYSDATE))
                  >= TRUNC(SYSDATE) - ?
            """;

        Long result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{days},
                Long.class
        );

        return result != null ? result : 0L;
    }

    // =====================================================
    // الإيرادات خلال الفترة
    // =====================================================

    public BigDecimal calculatePeriodRevenue(int days) {

        String sql = """
            SELECT NVL(SUM(PRICE), 0)
            FROM SESSION_RECORD
            WHERE SESSION_DATE >=
                  TRUNC(SYSDATE) - ?
            """;

        BigDecimal result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{days},
                BigDecimal.class
        );

        return result != null
                ? result
                : BigDecimal.ZERO;
    }
    // =====================================================
    // متوسط مدة الجلسة خلال الفترة
    // =====================================================

    public double calculateAverageSessionDuration(int days) {

        String sql = """
            SELECT NVL(
                AVG(
                    TO_NUMBER(
                        SUBSTR(
                            DURATION,
                            1,
                            INSTR(DURATION, ':') - 1
                        )
                    ) * 60
                    +
                    TO_NUMBER(
                        SUBSTR(
                            DURATION,
                            INSTR(DURATION, ':') + 1
                        )
                    )
                ),
                0
            )
            FROM SESSION_RECORD
            WHERE SESSION_DATE >=
                  TRUNC(SYSDATE) - ?
              AND REGEXP_LIKE(
                    TRIM(DURATION),
                    '^[0-9]{2}:[0-9]{2}$'
                  )
            """;

        Double result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{days},
                Double.class
        );

        return result != null
                ? result
                : 0.0;
    }
    // =====================================================
    // اتجاه الجلسات
    // =====================================================

    public List<SessionTrendResponse> getSessionTrend(
            int days
    ) {

        String sql = """
                SELECT
                    TRUNC(SESSION_DATE) AS SESSION_DAY,
                    COUNT(*) AS SESSION_COUNT
                FROM SESSION_RECORD
                WHERE SESSION_DATE >=
                      TRUNC(SYSDATE) - ?
                GROUP BY
                    TRUNC(SESSION_DATE)
                ORDER BY
                    SESSION_DAY
                """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setInt(
                        1,
                        days
                ),
                (rs, rowNum) -> {

                    java.sql.Date date =
                            rs.getDate(
                                    "SESSION_DAY"
                            );

                    return new SessionTrendResponse(
                            date != null
                                    ? date.toLocalDate()
                                    : null,

                            rs.getLong(
                                    "SESSION_COUNT"
                            )
                    );
                }
        );
    }

    // =====================================================
    // حالة الخطط الحالية
    // =====================================================

    public List<PlanStatusResponse> getPlanStatuses() {

        String sql = """
                SELECT
                    PLAN_STATUS,
                    COUNT(*) AS PLAN_COUNT
                FROM NUTRITIONPLAN
                GROUP BY PLAN_STATUS
                ORDER BY PLAN_STATUS
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new PlanStatusResponse(
                                rs.getString(
                                        "PLAN_STATUS"
                                ),
                                rs.getLong(
                                        "PLAN_COUNT"
                                )
                        )
        );
    }

    // =====================================================
    // الأمراض المزمنة
    // =====================================================

    public List<ChronicDiseaseReportResponse>
    getChronicDiseaseStatistics() {

        String sql = """
                SELECT
                    CD.CHRONIC_DISEASES_NAME,
                    COUNT(
                        DISTINCT CCD.CLIENT_ID
                    ) AS PATIENT_COUNT
                FROM CHRONIC_DISEASE CD
                JOIN CLIENT_CHRONIC_DISEASE CCD
                    ON CCD.CHRONIC_DISEASES_ID =
                       CD.CHRONIC_DISEASES_ID
                GROUP BY
                    CD.CHRONIC_DISEASES_NAME
                ORDER BY
                    PATIENT_COUNT DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new ChronicDiseaseReportResponse(
                                rs.getString(
                                        "CHRONIC_DISEASES_NAME"
                                ),
                                rs.getLong(
                                        "PATIENT_COUNT"
                                )
                        )
        );
    }

    // =====================================================
    // الحساسية الغذائية
    // =====================================================

    public List<AllergyReportResponse>
    getAllergyStatistics() {

        String sql = """
                SELECT
                    CA.ALLERGY_NAME,
                    COUNT(
                        DISTINCT AC.CLIENT_ID
                    ) AS PATIENT_COUNT
                FROM CLIENT_ALLERGY CA
                JOIN ALLERGIC_CLIENT AC
                    ON AC.CLIENT_ALLERGY_ID =
                       CA.CLIENT_ALLERGY_ID
                GROUP BY
                    CA.ALLERGY_NAME
                ORDER BY
                    PATIENT_COUNT DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new AllergyReportResponse(
                                rs.getString(
                                        "ALLERGY_NAME"
                                ),
                                rs.getLong(
                                        "PATIENT_COUNT"
                                )
                        )
        );
    }

    // =====================================================
    // تطور جسم المريض
    // =====================================================

    public List<BodyProgressResponse> getBodyProgress(
            Long clientId
    ) {

        validateClientId(clientId);

        String sql = """
                SELECT
                    MEASUREMENT_DATE,
                    WEIGHT,
                    BODY_FAT,
                    MUSCLE_MASS,
                    SMM,
                    WAIST_C,
                    HIP_C
                FROM BODY_DATA
                WHERE CLIENT_ID = ?
                ORDER BY MEASUREMENT_DATE
                """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setLong(
                        1,
                        clientId
                ),
                (rs, rowNum) -> {

                    java.sql.Date date =
                            rs.getDate(
                                    "MEASUREMENT_DATE"
                            );

                    return new BodyProgressResponse(
                            date != null
                                    ? date.toLocalDate()
                                    : null,

                            getNullableDouble(
                                    rs,
                                    "WEIGHT"
                            ),

                            getNullableDouble(
                                    rs,
                                    "BODY_FAT"
                            ),

                            getNullableDouble(
                                    rs,
                                    "MUSCLE_MASS"
                            ),

                            getNullableDouble(
                                    rs,
                                    "SMM"
                            ),

                            getNullableDouble(
                                    rs,
                                    "WAIST_C"
                            ),

                            getNullableDouble(
                                    rs,
                                    "HIP_C"
                            )
                    );
                }
        );
    }

    // =====================================================
    // ملخص تطور جسم المريض
    // =====================================================

    public PatientProgressSummary getPatientProgressSummary(
            Long clientId
    ) {

        validateClientId(clientId);

        String sql = """
                SELECT
                    FIRST_WEIGHT,
                    CURRENT_WEIGHT,
                    FIRST_BODY_FAT,
                    CURRENT_BODY_FAT,
                    FIRST_MUSCLE_MASS,
                    CURRENT_MUSCLE_MASS
                FROM (
                    SELECT
                        FIRST_VALUE(WEIGHT)
                            OVER (
                                ORDER BY MEASUREMENT_DATE
                            ) AS FIRST_WEIGHT,

                        LAST_VALUE(WEIGHT)
                            OVER (
                                ORDER BY MEASUREMENT_DATE
                                ROWS BETWEEN
                                    UNBOUNDED PRECEDING
                                    AND UNBOUNDED FOLLOWING
                            ) AS CURRENT_WEIGHT,

                        FIRST_VALUE(BODY_FAT)
                            OVER (
                                ORDER BY MEASUREMENT_DATE
                            ) AS FIRST_BODY_FAT,

                        LAST_VALUE(BODY_FAT)
                            OVER (
                                ORDER BY MEASUREMENT_DATE
                                ROWS BETWEEN
                                    UNBOUNDED PRECEDING
                                    AND UNBOUNDED FOLLOWING
                            ) AS CURRENT_BODY_FAT,

                        FIRST_VALUE(MUSCLE_MASS)
                            OVER (
                                ORDER BY MEASUREMENT_DATE
                            ) AS FIRST_MUSCLE_MASS,

                        LAST_VALUE(MUSCLE_MASS)
                            OVER (
                                ORDER BY MEASUREMENT_DATE
                                ROWS BETWEEN
                                    UNBOUNDED PRECEDING
                                    AND UNBOUNDED FOLLOWING
                            ) AS CURRENT_MUSCLE_MASS

                    FROM BODY_DATA
                    WHERE CLIENT_ID = ?
                )
                FETCH FIRST 1 ROW ONLY
                """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setLong(
                        1,
                        clientId
                ),
                rs -> {

                    PatientProgressSummary response =
                            new PatientProgressSummary();

                    if (!rs.next()) {
                        return response;
                    }

                    Double firstWeight =
                            getNullableDouble(
                                    rs,
                                    "FIRST_WEIGHT"
                            );

                    Double currentWeight =
                            getNullableDouble(
                                    rs,
                                    "CURRENT_WEIGHT"
                            );

                    Double firstBodyFat =
                            getNullableDouble(
                                    rs,
                                    "FIRST_BODY_FAT"
                            );

                    Double currentBodyFat =
                            getNullableDouble(
                                    rs,
                                    "CURRENT_BODY_FAT"
                            );

                    Double firstMuscleMass =
                            getNullableDouble(
                                    rs,
                                    "FIRST_MUSCLE_MASS"
                            );

                    Double currentMuscleMass =
                            getNullableDouble(
                                    rs,
                                    "CURRENT_MUSCLE_MASS"
                            );

                    response.setFirstWeight(
                            firstWeight
                    );

                    response.setCurrentWeight(
                            currentWeight
                    );

                    response.setWeightChange(
                            difference(
                                    currentWeight,
                                    firstWeight
                            )
                    );

                    response.setFirstBodyFat(
                            firstBodyFat
                    );

                    response.setCurrentBodyFat(
                            currentBodyFat
                    );

                    response.setBodyFatChange(
                            difference(
                                    currentBodyFat,
                                    firstBodyFat
                            )
                    );

                    response.setFirstMuscleMass(
                            firstMuscleMass
                    );

                    response.setCurrentMuscleMass(
                            currentMuscleMass
                    );

                    response.setMuscleMassChange(
                            difference(
                                    currentMuscleMass,
                                    firstMuscleMass
                            )
                    );

                    return response;
                }
        );
    }

    // =====================================================
    // آخر الجلسات
    // =====================================================

    public List<RecentSessionResponse> getRecentSessions(
            int limit
    ) {

        String sql = """
                SELECT
                    SESSION_ID,
                    CLIENT_ID,
                    FIRST_NAME,
                    LAST_NAME,
                    SESSION_DATE,
                    DURATION,
                    PRICE,
                    NOTES
                FROM (
                    SELECT
                        SR.SESSION_ID,
                        SR.CLIENT_ID,
                        C.FIRST_NAME,
                        C.LAST_NAME,
                        SR.SESSION_DATE,
                        SR.DURATION,
                        SR.PRICE,
                        SR.NOTES
                    FROM SESSION_RECORD SR
                    JOIN CLIENT C
                        ON C.CLIENT_ID =
                           SR.CLIENT_ID
                    ORDER BY
                        SR.SESSION_DATE DESC
                )
                WHERE ROWNUM <= ?
                """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setInt(
                        1,
                        limit
                ),
                (rs, rowNum) -> {

                    java.sql.Timestamp timestamp =
                            rs.getTimestamp(
                                    "SESSION_DATE"
                            );

                    String firstName =
                            rs.getString(
                                    "FIRST_NAME"
                            );

                    String lastName =
                            rs.getString(
                                    "LAST_NAME"
                            );

                    String fullName =
                            ((firstName != null
                                    ? firstName
                                    : "")
                                    + " "
                                    + (lastName != null
                                    ? lastName
                                    : ""))
                                    .trim();

                    return new RecentSessionResponse(
                            rs.getLong(
                                    "SESSION_ID"
                            ),

                            rs.getLong(
                                    "CLIENT_ID"
                            ),

                            fullName,

                            timestamp != null
                                    ? timestamp.toLocalDateTime()
                                    : null,

                            rs.getString(
                                    "DURATION"
                            ),

                            rs.getObject(
                                    "PRICE",
                                    Double.class
                            ),

                            rs.getString(
                                    "NOTES"
                            )
                    );
                }
        );
    }

    // =====================================================
    // اتجاه الإيرادات
    // =====================================================

    public List<RevenueTrendResponse> getRevenueTrend(
            int days
    ) {

        String sql = """
                SELECT
                    TRUNC(SESSION_DATE)
                        AS SESSION_DAY,

                    NVL(SUM(PRICE), 0)
                        AS DAILY_REVENUE

                FROM SESSION_RECORD

                WHERE SESSION_DATE >=
                      TRUNC(SYSDATE) - ?

                GROUP BY
                    TRUNC(SESSION_DATE)

                ORDER BY
                    SESSION_DAY
                """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setInt(
                        1,
                        days
                ),
                (rs, rowNum) -> {

                    java.sql.Date date =
                            rs.getDate(
                                    "SESSION_DAY"
                            );

                    return new RevenueTrendResponse(
                            date != null
                                    ? date.toLocalDate()
                                    : null,

                            rs.getDouble(
                                    "DAILY_REVENUE"
                            )
                    );
                }
        );
    }

    // =====================================================
    // التنبيهات
    // =====================================================

    public List<ReportAlertResponse> getAlerts() {

        List<ReportAlertResponse> alerts =
                new ArrayList<>();

        // =================================================
        // خطط ستنتهي خلال 3 أيام
        // =================================================

        String expiringPlansSql = """
                SELECT COUNT(*)
                FROM NUTRITIONPLAN
                WHERE PLAN_STATUS = 'Active'
                  AND END_DATE IS NOT NULL
                  AND END_DATE BETWEEN
                        TRUNC(SYSDATE)
                        AND TRUNC(SYSDATE) + 3
                """;

        Long expiringPlans =
                jdbcTemplate.queryForObject(
                        expiringPlansSql,
                        Long.class
                );

        if (expiringPlans != null
                && expiringPlans > 0) {

            alerts.add(
                    new ReportAlertResponse(
                            "EXPIRING_PLANS",
                            "WARNING",
                            "خطط غذائية قريبة من الانتهاء",
                            "توجد خطط غذائية ستنتهي خلال 3 أيام.",
                            expiringPlans
                    )
            );
        }

        // =================================================
        // مرضى لم تتم متابعتهم منذ 30 يومًا
        // =================================================

        String inactiveClientsSql = """
                SELECT COUNT(*)
                FROM CLIENT C
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM SESSION_RECORD SR
                    WHERE SR.CLIENT_ID =
                          C.CLIENT_ID
                      AND SR.SESSION_DATE >=
                          TRUNC(SYSDATE) - 30
                )
                """;

        Long inactiveClients =
                jdbcTemplate.queryForObject(
                        inactiveClientsSql,
                        Long.class
                );

        if (inactiveClients != null
                && inactiveClients > 0) {

            alerts.add(
                    new ReportAlertResponse(
                            "INACTIVE_CLIENTS",
                            "INFO",
                            "مرضى بحاجة إلى متابعة",
                            "يوجد مرضى لم تتم لهم جلسة خلال آخر 30 يومًا.",
                            inactiveClients
                    )
            );
        }

        // =================================================
        // بيانات جسم ناقصة
        // =================================================

        String incompleteBodyDataSql = """
                SELECT COUNT(*)
                FROM SESSION_RECORD SR
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM BODY_DATA BD
                    WHERE BD.SESSION_ID =
                          SR.SESSION_ID
                )
                AND SR.SESSION_DATE >=
                    TRUNC(SYSDATE) - 30
                """;

        Long incompleteBodyData =
                jdbcTemplate.queryForObject(
                        incompleteBodyDataSql,
                        Long.class
                );

        if (incompleteBodyData != null
                && incompleteBodyData > 0) {

            alerts.add(
                    new ReportAlertResponse(
                            "MISSING_BODY_DATA",
                            "WARNING",
                            "بيانات جسم ناقصة",
                            "توجد جلسات حديثة بدون بيانات قياسات جسم.",
                            incompleteBodyData
                    )
            );
        }

        // =================================================
        // جلسات بدون خطة غذائية
        // =================================================

        String sessionsWithoutPlanSql = """
                SELECT COUNT(*)
                FROM SESSION_RECORD SR
                WHERE SR.SESSION_DATE >=
                      TRUNC(SYSDATE) - 30
                  AND NOT EXISTS (
                      SELECT 1
                      FROM NUTRITIONPLAN NP
                      WHERE NP.SESSION_ID =
                            SR.SESSION_ID
                  )
                """;

        Long sessionsWithoutPlan =
                jdbcTemplate.queryForObject(
                        sessionsWithoutPlanSql,
                        Long.class
                );

        if (sessionsWithoutPlan != null
                && sessionsWithoutPlan > 0) {

            alerts.add(
                    new ReportAlertResponse(
                            "SESSIONS_WITHOUT_PLAN",
                            "INFO",
                            "جلسات بدون خطة غذائية",
                            "توجد جلسات حديثة لم يتم إنشاء خطة غذائية لها.",
                            sessionsWithoutPlan
                    )
            );
        }

        return alerts;
    }

    // =====================================================
    // Helpers
    // =====================================================

    private Double difference(
            Double current,
            Double first
    ) {

        if (current == null
                || first == null) {

            return null;
        }

        return current - first;
    }

    private Double getNullableDouble(
            java.sql.ResultSet rs,
            String column
    ) throws java.sql.SQLException {

        double value =
                rs.getDouble(column);

        return rs.wasNull()
                ? null
                : value;
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