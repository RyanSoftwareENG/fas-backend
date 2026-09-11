package com.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClinicSchemaProvisioningService {

    /*
     * Schema القالب.
     *
     * العيادة الأولى موجودة حاليًا في:
     *
     * FAS_ADMIN
     *
     * نستخدمها لقراءة البنية فقط.
     */
    private static final String TEMPLATE_SCHEMA =
            "FAS_ADMIN";

    private final String adminUrl;
    private final String adminUsername;
    private final String adminPassword;

    public ClinicSchemaProvisioningService(
            @Value("${fas.oracle.admin.url}") String adminUrl,
            @Value("${fas.oracle.admin.username}") String adminUsername,
            @Value("${fas.oracle.admin.password}") String adminPassword
    ) {

        this.adminUrl =
                adminUrl;

        this.adminUsername =
                adminUsername;

        this.adminPassword =
                adminPassword;
    }

    // =========================================================
    // تجهيز Schema كامل للعيادة
    // =========================================================

    public void provisionClinicSchema(
            Long clinicId
    ) {

        if (clinicId == null || clinicId <= 0) {

            throw new IllegalArgumentException(
                    "رقم العيادة غير صحيح"
            );
        }

        String schemaName =
                "FAS_CLINIC_" + clinicId;

        validateIdentifier(
                schemaName
        );

        String clinicPassword =
                generateClinicPassword(
                        clinicId
                );

        try {

            // =================================================
            // 1. إنشاء User / Schema
            // =================================================

            createClinicUser(
                    schemaName,
                    clinicPassword
            );

            // =================================================
            // 2. إنشاء كل Objects الخاصة بالعيادة
            // =================================================

            createClinicObjects(
                    schemaName,
                    clinicPassword
            );

        } catch (Exception e) {

            /*
             * إذا فشل أي جزء:
             *
             * نحذف Schema بالكامل.
             */
            try {

                dropSchema(
                        schemaName
                );

            } catch (Exception cleanupException) {

                e.addSuppressed(
                        cleanupException
                );
            }

            throw new IllegalStateException(
                    "فشل تجهيز قاعدة بيانات العيادة: "
                            + schemaName,
                    e
            );
        }
    }

    // =========================================================
    // إنشاء User / Schema
    // =========================================================

    private void createClinicUser(
            String schemaName,
            String clinicPassword
    ) throws SQLException {

        String sql =
                "CREATE USER "
                        + schemaName
                        + " IDENTIFIED BY \""
                        + clinicPassword
                        + "\"";

        try (
                Connection connection =
                        DriverManager.getConnection(
                                adminUrl,
                                adminUsername,
                                adminPassword
                        );

                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(
                    sql
            );

            grant(
                    statement,
                    schemaName,
                    "CREATE SESSION"
            );

            grant(
                    statement,
                    schemaName,
                    "CREATE TABLE"
            );

            grant(
                    statement,
                    schemaName,
                    "CREATE SEQUENCE"
            );

            grant(
                    statement,
                    schemaName,
                    "CREATE VIEW"
            );

            grant(
                    statement,
                    schemaName,
                    "CREATE PROCEDURE"
            );

            /*
             * أثناء التطوير المحلي.
             */
            statement.executeUpdate(
                    "GRANT UNLIMITED TABLESPACE TO "
                            + schemaName
            );
        }
    }

    // =========================================================
    // GRANT
    // =========================================================

    private void grant(
            Statement statement,
            String schemaName,
            String privilege
    ) throws SQLException {

        statement.executeUpdate(
                "GRANT "
                        + privilege
                        + " TO "
                        + schemaName
        );
    }

    // =========================================================
    // إنشاء Objects الخاصة بالعيادة
    // =========================================================

    private void createClinicObjects(
            String schemaName,
            String clinicPassword
    ) throws SQLException {

        /*
         * هذا الاتصال:
         *
         * FAS_PROVISIONER
         *
         * يستخدم فقط لقراءة Metadata
         * من FAS_ADMIN.
         */
        try (
                Connection metadataConnection =
                        DriverManager.getConnection(
                                adminUrl,
                                adminUsername,
                                adminPassword
                        );

                /*
                 * هذا الاتصال:
                 *
                 * FAS_CLINIC_<ID>
                 *
                 * يستخدم لإنشاء objects.
                 */
                Connection targetConnection =
                        DriverManager.getConnection(
                                adminUrl,
                                schemaName,
                                clinicPassword
                        )
        ) {

            // -------------------------------------------------
            // 1. إعداد Metadata
            // -------------------------------------------------

            configureMetadata(
                    metadataConnection
            );

            // -------------------------------------------------
            // 2. قراءة الجداول
            // -------------------------------------------------

            List<String> tables =
                    getTables(
                            metadataConnection,
                            TEMPLATE_SCHEMA
                    );

            if (tables.isEmpty()) {

                throw new SQLException(
                        "لم يتم العثور على جداول في "
                                + TEMPLATE_SCHEMA
                );
            }

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "Template Schema: "
                            + TEMPLATE_SCHEMA
            );

            System.out.println(
                    "Target Schema: "
                            + schemaName
            );

            System.out.println(
                    "Tables found: "
                            + tables.size()
            );

            System.out.println(
                    "=========================================="
            );

            // -------------------------------------------------
            // 3. إنشاء الجداول
            // -------------------------------------------------

            for (String tableName : tables) {

                System.out.println(
                        "Creating table: "
                                + tableName
                );

                String ddl =
                        getTableDdl(
                                metadataConnection,
                                TEMPLATE_SCHEMA,
                                tableName
                        );

                ddl =
                        cleanTableDdl(
                                ddl,
                                TEMPLATE_SCHEMA
                        );

                executeDdl(
                        targetConnection,
                        ddl
                );
            }

            System.out.println(
                    "All tables created successfully."
            );

            // -------------------------------------------------
            // 4. Primary Keys
            // -------------------------------------------------

            copyPrimaryKeys(
                    metadataConnection,
                    targetConnection,
                    TEMPLATE_SCHEMA
            );

            System.out.println(
                    "Primary keys created successfully."
            );

            // -------------------------------------------------
            // 5. Unique Constraints
            // -------------------------------------------------

            copyUniqueConstraints(
                    metadataConnection,
                    targetConnection,
                    TEMPLATE_SCHEMA
            );

            System.out.println(
                    "Unique constraints created successfully."
            );

            // -------------------------------------------------
            // 6. Check Constraints
            // -------------------------------------------------

            copyCheckConstraints(
                    metadataConnection,
                    targetConnection,
                    TEMPLATE_SCHEMA
            );

            System.out.println(
                    "Check constraints created successfully."
            );

            // -------------------------------------------------
            // 7. Foreign Keys
            // -------------------------------------------------

            copyForeignKeys(
                    metadataConnection,
                    targetConnection,
                    TEMPLATE_SCHEMA
            );

            System.out.println(
                    "Foreign keys created successfully."
            );

            // -------------------------------------------------
            // 8. Indexes الإضافية
            // -------------------------------------------------

            copyIndexes(
                    metadataConnection,
                    targetConnection,
                    TEMPLATE_SCHEMA
            );

            System.out.println(
                    "Additional indexes processed successfully."
            );

            // -------------------------------------------------
            // 9. Sequences المخصصة
            // -------------------------------------------------

            copySequences(
                    metadataConnection,
                    targetConnection,
                    TEMPLATE_SCHEMA
            );

            System.out.println(
                    "Sequences processed successfully."
            );

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "Clinic Schema provisioning completed:"
                            + " "
                            + schemaName
            );

            System.out.println(
                    "=========================================="
            );
        }
    }

    // =========================================================
    // DBMS_METADATA
    // =========================================================

    private void configureMetadata(
            Connection connection
    ) throws SQLException {

        String sql = """
                BEGIN

                    DBMS_METADATA.SET_TRANSFORM_PARAM(
                        DBMS_METADATA.SESSION_TRANSFORM,
                        'SQLTERMINATOR',
                        FALSE
                    );

                    DBMS_METADATA.SET_TRANSFORM_PARAM(
                        DBMS_METADATA.SESSION_TRANSFORM,
                        'SEGMENT_ATTRIBUTES',
                        FALSE
                    );

                    DBMS_METADATA.SET_TRANSFORM_PARAM(
                        DBMS_METADATA.SESSION_TRANSFORM,
                        'STORAGE',
                        FALSE
                    );

                    DBMS_METADATA.SET_TRANSFORM_PARAM(
                        DBMS_METADATA.SESSION_TRANSFORM,
                        'TABLESPACE',
                        FALSE
                    );

                    /*
                     * لا نريد Constraints داخل
                     * CREATE TABLE.
                     */
                    DBMS_METADATA.SET_TRANSFORM_PARAM(
                        DBMS_METADATA.SESSION_TRANSFORM,
                        'CONSTRAINTS',
                        FALSE
                    );

                    /*
                     * لا نريد Foreign Keys داخل
                     * CREATE TABLE.
                     */
                    DBMS_METADATA.SET_TRANSFORM_PARAM(
                        DBMS_METADATA.SESSION_TRANSFORM,
                        'REF_CONSTRAINTS',
                        FALSE
                    );

                END;
                """;

        try (
                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(
                    sql
            );
        }
    }

    // =========================================================
    // قراءة الجداول
    // =========================================================

    private List<String> getTables(
            Connection connection,
            String schemaName
    ) throws SQLException {

        String sql = """
                SELECT TABLE_NAME
                FROM DBA_TABLES
                WHERE OWNER = ?
                ORDER BY TABLE_NAME
                """;

        List<String> tables =
                new ArrayList<>();

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    schemaName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    tables.add(
                            resultSet.getString(
                                    "TABLE_NAME"
                            )
                    );
                }
            }
        }

        return tables;
    }

    // =========================================================
    // DDL الجدول
    // =========================================================

    private String getTableDdl(
            Connection connection,
            String schemaName,
            String tableName
    ) throws SQLException {

        String sql = """
                SELECT DBMS_METADATA.GET_DDL(
                    'TABLE',
                    ?,
                    ?
                )
                FROM DUAL
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    tableName
            );

            statement.setString(
                    2,
                    schemaName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {

                    throw new SQLException(
                            "تعذر الحصول على DDL للجدول: "
                                    + tableName
                    );
                }

                return resultSet.getString(1);
            }
        }
    }

    // =========================================================
    // تنظيف DDL الجدول
    // =========================================================

    private String cleanTableDdl(
            String ddl,
            String sourceSchema
    ) {

        if (ddl == null) {
            return null;
        }

        String cleaned =
                ddl.trim();

        cleaned =
                cleaned.replace(
                        "\"" + sourceSchema + "\".",
                        ""
                );

        cleaned =
                cleaned.replace(
                        sourceSchema + ".",
                        ""
                );

        if (cleaned.endsWith("/")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        if (cleaned.endsWith(";")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        return cleaned;
    }

    // =========================================================
    // Primary Keys
    // =========================================================

    private void copyPrimaryKeys(
            Connection metadataConnection,
            Connection targetConnection,
            String sourceSchema
    ) throws SQLException {

        String sql = """
                SELECT
                    CONSTRAINT_NAME,
                    TABLE_NAME
                FROM DBA_CONSTRAINTS
                WHERE OWNER = ?
                  AND CONSTRAINT_TYPE = 'P'
                ORDER BY TABLE_NAME, CONSTRAINT_NAME
                """;

        try (
                PreparedStatement statement =
                        metadataConnection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    sourceSchema
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    String constraintName =
                            resultSet.getString(
                                    "CONSTRAINT_NAME"
                            );

                    String tableName =
                            resultSet.getString(
                                    "TABLE_NAME"
                            );

                    List<String> columns =
                            getConstraintColumns(
                                    metadataConnection,
                                    sourceSchema,
                                    constraintName
                            );

                    String ddl =
                            buildAddConstraintSql(
                                    tableName,
                                    constraintName,
                                    "PRIMARY KEY",
                                    columns
                            );

                    System.out.println(
                            "Creating PK: "
                                    + constraintName
                    );

                    executeDdl(
                            targetConnection,
                            ddl
                    );
                }
            }
        }
    }

    // =========================================================
    // Unique Constraints
    // =========================================================

    private void copyUniqueConstraints(
            Connection metadataConnection,
            Connection targetConnection,
            String sourceSchema
    ) throws SQLException {

        String sql = """
                SELECT
                    CONSTRAINT_NAME,
                    TABLE_NAME
                FROM DBA_CONSTRAINTS
                WHERE OWNER = ?
                  AND CONSTRAINT_TYPE = 'U'
                ORDER BY TABLE_NAME, CONSTRAINT_NAME
                """;

        try (
                PreparedStatement statement =
                        metadataConnection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    sourceSchema
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    String constraintName =
                            resultSet.getString(
                                    "CONSTRAINT_NAME"
                            );

                    String tableName =
                            resultSet.getString(
                                    "TABLE_NAME"
                            );

                    List<String> columns =
                            getConstraintColumns(
                                    metadataConnection,
                                    sourceSchema,
                                    constraintName
                            );

                    String ddl =
                            buildAddConstraintSql(
                                    tableName,
                                    constraintName,
                                    "UNIQUE",
                                    columns
                            );

                    System.out.println(
                            "Creating UNIQUE: "
                                    + constraintName
                    );

                    executeDdl(
                            targetConnection,
                            ddl
                    );
                }
            }
        }
    }

    // =========================================================
    // Check Constraints
    // =========================================================

    private void copyCheckConstraints(
            Connection metadataConnection,
            Connection targetConnection,
            String sourceSchema
    ) throws SQLException {

        String sql = """
                SELECT
                    CONSTRAINT_NAME,
                    TABLE_NAME,
                    SEARCH_CONDITION
                FROM DBA_CONSTRAINTS
                WHERE OWNER = ?
                  AND CONSTRAINT_TYPE = 'C'
                ORDER BY TABLE_NAME, CONSTRAINT_NAME
                """;

        try (
                PreparedStatement statement =
                        metadataConnection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    sourceSchema
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    String constraintName =
                            resultSet.getString(
                                    "CONSTRAINT_NAME"
                            );

                    String tableName =
                            resultSet.getString(
                                    "TABLE_NAME"
                            );

                    String searchCondition =
                            resultSet.getString(
                                    "SEARCH_CONDITION"
                            );

                    /*
                     * Oracle يخزن NOT NULL أيضًا كـCHECK
                     * أحيانًا.
                     *
                     * تعريف الأعمدة الذي أنشأناه يحتوي
                     * بالفعل على NOT NULL، لذلك لا نكررها.
                     */
                    if (isNotNullConstraint(
                            searchCondition
                    )) {
                        continue;
                    }

                    if (searchCondition == null
                            || searchCondition.isBlank()) {

                        continue;
                    }

                    String ddl =
                            "ALTER TABLE "
                                    + quote(tableName)
                                    + " ADD CONSTRAINT "
                                    + quote(constraintName)
                                    + " CHECK ("
                                    + searchCondition
                                    + ")";

                    System.out.println(
                            "Creating CHECK: "
                                    + constraintName
                    );

                    executeDdl(
                            targetConnection,
                            ddl
                    );
                }
            }
        }
    }

    // =========================================================
    // اكتشاف NOT NULL
    // =========================================================

    private boolean isNotNullConstraint(
            String searchCondition
    ) {

        if (searchCondition == null) {
            return false;
        }

        String normalized =
                searchCondition
                        .replace(
                                "\"",
                                ""
                        )
                        .replace(
                                " ",
                                ""
                        )
                        .toUpperCase();

        return normalized.matches(
                ".*ISNOTNULL.*"
        );
    }

    // =========================================================
    // Foreign Keys
    // =========================================================

    private void copyForeignKeys(
            Connection metadataConnection,
            Connection targetConnection,
            String sourceSchema
    ) throws SQLException {

        String sql = """
                SELECT
                    CONSTRAINT_NAME,
                    TABLE_NAME,
                    R_CONSTRAINT_NAME,
                    DELETE_RULE,
                    DEFERRABLE,
                    DEFERRED
                FROM DBA_CONSTRAINTS
                WHERE OWNER = ?
                  AND CONSTRAINT_TYPE = 'R'
                ORDER BY TABLE_NAME, CONSTRAINT_NAME
                """;

        try (
                PreparedStatement statement =
                        metadataConnection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    sourceSchema
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    String constraintName =
                            resultSet.getString(
                                    "CONSTRAINT_NAME"
                            );

                    String tableName =
                            resultSet.getString(
                                    "TABLE_NAME"
                            );

                    String referencedConstraint =
                            resultSet.getString(
                                    "R_CONSTRAINT_NAME"
                            );

                    String deleteRule =
                            resultSet.getString(
                                    "DELETE_RULE"
                            );

                    String deferrable =
                            resultSet.getString(
                                    "DEFERRABLE"
                            );

                    String deferred =
                            resultSet.getString(
                                    "DEFERRED"
                            );

                    String referencedTable =
                            getReferencedTable(
                                    metadataConnection,
                                    sourceSchema,
                                    referencedConstraint
                            );

                    List<String> columns =
                            getConstraintColumns(
                                    metadataConnection,
                                    sourceSchema,
                                    constraintName
                            );

                    List<String> referencedColumns =
                            getConstraintColumns(
                                    metadataConnection,
                                    sourceSchema,
                                    referencedConstraint
                            );

                    String ddl =
                            buildForeignKeySql(
                                    tableName,
                                    constraintName,
                                    columns,
                                    referencedTable,
                                    referencedColumns,
                                    deleteRule,
                                    deferrable,
                                    deferred
                            );

                    System.out.println(
                            "Creating FK: "
                                    + constraintName
                    );

                    executeDdl(
                            targetConnection,
                            ddl
                    );
                }
            }
        }
    }

    // =========================================================
    // الجدول المرجعي
    // =========================================================

    private String getReferencedTable(
            Connection connection,
            String sourceSchema,
            String constraintName
    ) throws SQLException {

        String sql = """
                SELECT TABLE_NAME
                FROM DBA_CONSTRAINTS
                WHERE OWNER = ?
                  AND CONSTRAINT_NAME = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    sourceSchema
            );

            statement.setString(
                    2,
                    constraintName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {

                    throw new SQLException(
                            "لم يتم العثور على الجدول المرجعي للـConstraint: "
                                    + constraintName
                    );
                }

                return resultSet.getString(
                        "TABLE_NAME"
                );
            }
        }
    }

    // =========================================================
    // أعمدة Constraint
    // =========================================================

    private List<String> getConstraintColumns(
            Connection connection,
            String sourceSchema,
            String constraintName
    ) throws SQLException {

        String sql = """
                SELECT COLUMN_NAME
                FROM DBA_CONS_COLUMNS
                WHERE OWNER = ?
                  AND CONSTRAINT_NAME = ?
                ORDER BY POSITION
                """;

        List<String> columns =
                new ArrayList<>();

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    sourceSchema
            );

            statement.setString(
                    2,
                    constraintName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    columns.add(
                            resultSet.getString(
                                    "COLUMN_NAME"
                            )
                    );
                }
            }
        }

        return columns;
    }

    // =========================================================
    // بناء PK / UNIQUE
    // =========================================================

    private String buildAddConstraintSql(
            String tableName,
            String constraintName,
            String constraintType,
            List<String> columns
    ) {

        if (columns.isEmpty()) {

            throw new IllegalArgumentException(
                    "لا توجد أعمدة للـConstraint: "
                            + constraintName
            );
        }

        StringBuilder sql =
                new StringBuilder();

        sql.append(
                "ALTER TABLE "
        );

        sql.append(
                quote(tableName)
        );

        sql.append(
                " ADD CONSTRAINT "
        );

        sql.append(
                quote(constraintName)
        );

        sql.append(
                " "
        );

        sql.append(
                constraintType
        );

        sql.append(
                " ("
        );

        appendColumns(
                sql,
                columns
        );

        sql.append(
                ")"
        );

        return sql.toString();
    }

    // =========================================================
    // بناء Foreign Key
    // =========================================================

    private String buildForeignKeySql(
            String tableName,
            String constraintName,
            List<String> columns,
            String referencedTable,
            List<String> referencedColumns,
            String deleteRule,
            String deferrable,
            String deferred
    ) {

        if (columns.isEmpty()) {

            throw new IllegalArgumentException(
                    "لا توجد أعمدة للـForeign Key: "
                            + constraintName
            );
        }

        if (referencedColumns.isEmpty()) {

            throw new IllegalArgumentException(
                    "لا توجد أعمدة مرجعية للـForeign Key: "
                            + constraintName
            );
        }

        StringBuilder sql =
                new StringBuilder();

        sql.append(
                "ALTER TABLE "
        );

        sql.append(
                quote(tableName)
        );

        sql.append(
                " ADD CONSTRAINT "
        );

        sql.append(
                quote(constraintName)
        );

        sql.append(
                " FOREIGN KEY ("
        );

        appendColumns(
                sql,
                columns
        );

        sql.append(
                ") REFERENCES "
        );

        sql.append(
                quote(referencedTable)
        );

        sql.append(
                " ("
        );

        appendColumns(
                sql,
                referencedColumns
        );

        sql.append(
                ")"
        );

        if ("CASCADE".equalsIgnoreCase(
                deleteRule
        )) {

            sql.append(
                    " ON DELETE CASCADE"
            );

        } else if ("SET NULL".equalsIgnoreCase(
                deleteRule
        )) {

            sql.append(
                    " ON DELETE SET NULL"
            );
        }

        /*
         * Deferrable.
         */
        if ("DEFERRABLE".equalsIgnoreCase(
                deferrable
        )) {

            if ("DEFERRED".equalsIgnoreCase(
                    deferred
            )) {

                sql.append(
                        " DEFERRABLE INITIALLY DEFERRED"
                );

            } else {

                sql.append(
                        " DEFERRABLE INITIALLY IMMEDIATE"
                );
            }
        } else {

            sql.append(
                    " NOT DEFERRABLE"
            );
        }

        return sql.toString();
    }

    // =========================================================
    // الأعمدة
    // =========================================================

    private void appendColumns(
            StringBuilder sql,
            List<String> columns
    ) {

        for (int i = 0;
             i < columns.size();
             i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append(
                    quote(
                            columns.get(i)
                    )
            );
        }
    }

    // =========================================================
    // Indexes إضافية
    // =========================================================

    private void copyIndexes(
            Connection metadataConnection,
            Connection targetConnection,
            String sourceSchema
    ) throws SQLException {

        List<String> indexes =
                getAdditionalIndexes(
                        metadataConnection,
                        sourceSchema
                );

        for (String indexName :
                indexes) {

            String ddl =
                    getIndexDdl(
                            metadataConnection,
                            sourceSchema,
                            indexName
                    );

            if (ddl == null
                    || ddl.isBlank()) {

                continue;
            }

            ddl =
                    cleanIndexDdl(
                            ddl,
                            sourceSchema
                    );

            try {

                executeDdl(
                        targetConnection,
                        ddl
                );

            } catch (SQLException e) {

                System.err.println(
                        "تعذر إنشاء Index: "
                                + indexName
                                + " - "
                                + e.getMessage()
                );
            }
        }
    }

    // =========================================================
    // Indexes غير المرتبطة بـPK/UQ
    // =========================================================

    private List<String> getAdditionalIndexes(
            Connection connection,
            String schemaName
    ) throws SQLException {

        String sql = """
                SELECT i.INDEX_NAME
                FROM DBA_INDEXES i
                WHERE i.OWNER = ?
                  AND i.INDEX_NAME NOT LIKE 'SYS_IL%'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM DBA_CONSTRAINTS c
                      WHERE c.OWNER = i.OWNER
                        AND c.CONSTRAINT_NAME = i.INDEX_NAME
                        AND c.CONSTRAINT_TYPE IN ('P', 'U')
                  )
                ORDER BY i.INDEX_NAME
                """;

        List<String> indexes =
                new ArrayList<>();

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    schemaName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    indexes.add(
                            resultSet.getString(
                                    "INDEX_NAME"
                            )
                    );
                }
            }
        }

        return indexes;
    }

    // =========================================================
    // DDL للـIndex
    // =========================================================

    private String getIndexDdl(
            Connection connection,
            String schemaName,
            String indexName
    ) throws SQLException {

        String sql = """
                SELECT DBMS_METADATA.GET_DDL(
                    'INDEX',
                    ?,
                    ?
                )
                FROM DUAL
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    indexName
            );

            statement.setString(
                    2,
                    schemaName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return "";
                }

                String ddl =
                        resultSet.getString(1);

                return ddl == null
                        ? ""
                        : ddl;
            }
        }
    }

    // =========================================================
    // تنظيف Index DDL
    // =========================================================

    private String cleanIndexDdl(
            String ddl,
            String sourceSchema
    ) {

        if (ddl == null) {
            return null;
        }

        String cleaned =
                ddl
                        .replace(
                                "\"" + sourceSchema + "\".",
                                ""
                        )
                        .replace(
                                sourceSchema + ".",
                                ""
                        )
                        .trim();

        if (cleaned.endsWith("/")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        if (cleaned.endsWith(";")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        return cleaned;
    }

    // =========================================================
    // Sequences المخصصة
    // =========================================================

    private void copySequences(
            Connection metadataConnection,
            Connection targetConnection,
            String sourceSchema
    ) throws SQLException {

        List<String> sequences =
                getCustomSequences(
                        metadataConnection,
                        sourceSchema
                );

        for (String sequenceName :
                sequences) {

            String ddl =
                    getSequenceDdl(
                            metadataConnection,
                            sourceSchema,
                            sequenceName
                    );

            if (ddl == null
                    || ddl.isBlank()) {

                continue;
            }

            ddl =
                    cleanSequenceDdl(
                            ddl,
                            sourceSchema
                    );

            executeDdl(
                    targetConnection,
                    ddl
            );
        }
    }

    // =========================================================
    // Sequences المخصصة فقط
    // =========================================================

    private List<String> getCustomSequences(
            Connection connection,
            String schemaName
    ) throws SQLException {

        String sql = """
                SELECT SEQUENCE_NAME
                FROM DBA_SEQUENCES
                WHERE SEQUENCE_OWNER = ?
                  AND SEQUENCE_NAME NOT LIKE 'ISEQ$$_%'
                ORDER BY SEQUENCE_NAME
                """;

        List<String> sequences =
                new ArrayList<>();

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    schemaName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    sequences.add(
                            resultSet.getString(
                                    "SEQUENCE_NAME"
                            )
                    );
                }
            }
        }

        return sequences;
    }

    // =========================================================
    // DDL للـSequence
    // =========================================================

    private String getSequenceDdl(
            Connection connection,
            String schemaName,
            String sequenceName
    ) throws SQLException {

        String sql = """
                SELECT DBMS_METADATA.GET_DDL(
                    'SEQUENCE',
                    ?,
                    ?
                )
                FROM DUAL
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    sequenceName
            );

            statement.setString(
                    2,
                    schemaName
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return "";
                }

                String ddl =
                        resultSet.getString(1);

                return ddl == null
                        ? ""
                        : ddl;
            }
        }
    }

    // =========================================================
    // تنظيف Sequence DDL
    // =========================================================

    private String cleanSequenceDdl(
            String ddl,
            String sourceSchema
    ) {

        if (ddl == null) {
            return null;
        }

        String cleaned =
                ddl
                        .replace(
                                "\"" + sourceSchema + "\".",
                                ""
                        )
                        .replace(
                                sourceSchema + ".",
                                ""
                        )
                        .trim();

        if (cleaned.endsWith("/")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        if (cleaned.endsWith(";")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        return cleaned;
    }

    // =========================================================
    // تنفيذ DDL
    // =========================================================

    private void executeDdl(
            Connection connection,
            String ddl
    ) throws SQLException {

        if (ddl == null || ddl.isBlank()) {
            return;
        }

        String cleaned =
                ddl.trim();

        if (cleaned.endsWith("/")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        if (cleaned.endsWith(";")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    ).trim();
        }

        try (
                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(
                    cleaned
            );
        }
    }

    // =========================================================
    // Quote Identifier
    // =========================================================

    private String quote(
            String value
    ) {

        return "\""
                + value.replace(
                "\"",
                "\"\""
        )
                + "\"";
    }

    // =========================================================
    // حذف Schema عند الفشل
    // =========================================================

    private void dropSchemaInternal(
            String schemaName
    ) throws SQLException {

        try (
                Connection connection =
                        DriverManager.getConnection(
                                adminUrl,
                                adminUsername,
                                adminPassword
                        );

                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(
                    "DROP USER "
                            + schemaName
                            + " CASCADE"
            );
        }
    }

    // =========================================================
    // حذف Schema
    // =========================================================

    public void dropSchema(
            String schemaName
    ) {

        validateIdentifier(
                schemaName
        );

        try {

            dropSchemaInternal(
                    schemaName
            );

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "فشل حذف Schema: "
                            + schemaName,
                    e
            );
        }
    }

    // =========================================================
    // كلمة مرور العيادة
    // =========================================================

    private String generateClinicPassword(
            Long clinicId
    ) {

        /*
         * مؤقتة للتطوير والاختبار فقط.
         */
        return "FAS_Clinic_"
                + clinicId
                + "_2026";
    }

    // =========================================================
    // التحقق من اسم Oracle
    // =========================================================

    private void validateIdentifier(
            String identifier
    ) {

        if (identifier == null
                || !identifier.matches(
                "[A-Z][A-Z0-9_$#]*"
        )) {

            throw new IllegalArgumentException(
                    "اسم Oracle غير صالح: "
                            + identifier
            );
        }
    }
}