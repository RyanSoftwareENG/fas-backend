package com.fas.tenant.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClinicSchemaResolver {

    private final JdbcTemplate managementJdbcTemplate;

    public ClinicSchemaResolver(JdbcTemplate managementJdbcTemplate) {
        this.managementJdbcTemplate = managementJdbcTemplate;
    }

    public String resolveSchemaByClinicId(Long clinicId) {

        if (clinicId == null) {
            throw new IllegalArgumentException(
                    "Clinic ID غير موجود."
            );
        }

        String sql = """
                SELECT SCHEMA_NAME
                FROM FAS_MANAGEMENT.CLINIC_DATABASE
                WHERE CLINIC_ID = ?
                  AND STATUS = 'ACTIVE'
                """;

        return managementJdbcTemplate.query(
                sql,
                ps -> ps.setLong(1, clinicId),
                rs -> {
                    if (!rs.next()) {
                        throw new IllegalArgumentException(
                                "لا توجد قاعدة بيانات نشطة لهذه العيادة."
                        );
                    }

                    return rs.getString("SCHEMA_NAME");
                }
        );
    }
}