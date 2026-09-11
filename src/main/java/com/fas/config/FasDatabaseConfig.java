package com.fas.config;

import com.admin.entity.ClinicDatabase;
import com.admin.repository.ClinicDatabaseRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.fas.repository",
        entityManagerFactoryRef = "fasEntityManagerFactory",
        transactionManagerRef = "fasTransactionManager"
)
public class FasDatabaseConfig {

    // =====================================================
    // DataSource Properties
    // =====================================================

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties fasDataSourceProperties() {

        return new DataSourceProperties();
    }

    // =====================================================
    // Base DataSource
    // =====================================================

    @Bean(name = "fasBaseDataSource")
    public DataSource fasBaseDataSource(
            @Qualifier("fasDataSourceProperties")
            DataSourceProperties properties
    ) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    // =====================================================
    // Clinic-aware DataSource
    // =====================================================

    @Bean
    @Primary
    public DataSource fasDataSource(
            @Qualifier("fasBaseDataSource")
            DataSource baseDataSource,

            ObjectProvider<ClinicDatabaseRepository>
                    clinicDatabaseRepositoryProvider
    ) {

        return new ClinicSchemaDataSource(
                baseDataSource,
                clinicDatabaseRepositoryProvider
        );
    }

    // =====================================================
    // EntityManagerFactory
    // =====================================================

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean fasEntityManagerFactory(
            EntityManagerFactoryBuilder builder,

            @Qualifier("fasDataSource")
            DataSource dataSource
    ) {

        Map<String, Object> jpaProperties =
                new HashMap<>();

        jpaProperties.put(
                "hibernate.hbm2ddl.auto",
                "none"
        );

        jpaProperties.put(
                "hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl"
        );

        jpaProperties.put(
                "hibernate.show_sql",
                true
        );

        jpaProperties.put(
                "hibernate.format_sql",
                true
        );

        return builder
                .dataSource(dataSource)
                .packages("com.fas.entity")
                .persistenceUnit("fasPersistenceUnit")
                .properties(jpaProperties)
                .build();
    }

    // =====================================================
    // Transaction Manager
    // =====================================================

    @Bean
    @Primary
    public PlatformTransactionManager fasTransactionManager(
            @Qualifier("fasEntityManagerFactory")
            EntityManagerFactory entityManagerFactory
    ) {

        return new JpaTransactionManager(
                entityManagerFactory
        );
    }

    // =====================================================
    // Clinic Schema DataSource
    // =====================================================

    private static class ClinicSchemaDataSource
            extends AbstractDataSource {

        private final DataSource targetDataSource;

        private final ObjectProvider<ClinicDatabaseRepository>
                clinicDatabaseRepositoryProvider;

        private ClinicSchemaDataSource(
                DataSource targetDataSource,

                ObjectProvider<ClinicDatabaseRepository>
                        clinicDatabaseRepositoryProvider
        ) {

            this.targetDataSource =
                    targetDataSource;

            this.clinicDatabaseRepositoryProvider =
                    clinicDatabaseRepositoryProvider;
        }

        @Override
        public Connection getConnection()
                throws SQLException {

            Connection connection =
                    targetDataSource.getConnection();

            return prepareConnection(connection);
        }

        @Override
        public Connection getConnection(
                String username,
                String password
        ) throws SQLException {

            Connection connection =
                    targetDataSource.getConnection(
                            username,
                            password
                    );

            return prepareConnection(connection);
        }

        private Connection prepareConnection(
                Connection connection
        ) throws SQLException {

            Long clinicId =
                    ClinicContext.getClinicId();

            /*
             * أثناء Startup أو أي عملية لا تملك ClinicContext
             * نستخدم Schema الحساب نفسه: FAS_ADMIN.
             */
            if (clinicId == null) {

                setCurrentSchema(
                        connection,
                        "FAS_ADMIN"
                );

                return connection;
            }

            ClinicDatabaseRepository repository =
                    clinicDatabaseRepositoryProvider.getObject();

            ClinicDatabase database =
                    repository
                            .findByClinicClinicId(clinicId)
                            .filter(item ->
                                    "ACTIVE".equalsIgnoreCase(
                                            item.getStatus()
                                    )
                            )
                            .orElseThrow(() -> {

                                try {
                                    connection.close();
                                } catch (SQLException ignored) {
                                }

                                return new IllegalStateException(
                                        "No active database found for clinic ID: "
                                                + clinicId
                                );
                            });

            String schemaName =
                    database.getSchemaName();

            validateSchemaName(schemaName);

            setCurrentSchema(
                    connection,
                    schemaName
            );

            return connection;
        }

        private void setCurrentSchema(
                Connection connection,
                String schemaName
        ) throws SQLException {

            validateSchemaName(schemaName);

            try (var statement =
                         connection.createStatement()) {

                statement.execute(
                        "ALTER SESSION SET CURRENT_SCHEMA = "
                                + schemaName
                );
            }
        }

        private void validateSchemaName(
                String schemaName
        ) throws SQLException {

            if (schemaName == null ||
                    schemaName.isBlank()) {

                throw new SQLException(
                        "Schema name is empty"
                );
            }

            if (!schemaName.matches(
                    "[A-Za-z][A-Za-z0-9_$#]*"
            )) {

                throw new SQLException(
                        "Invalid Oracle schema name: "
                                + schemaName
                );
            }
        }
    }
}