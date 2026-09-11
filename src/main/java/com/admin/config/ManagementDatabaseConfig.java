package com.admin.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.admin.repository",
        entityManagerFactoryRef = "managementEntityManagerFactory",
        transactionManagerRef = "managementTransactionManager"
)
public class ManagementDatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.management.datasource")
    public DataSourceProperties managementDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource managementDataSource(
            @Qualifier("managementDataSourceProperties")
            DataSourceProperties properties
    ) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean managementEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("managementDataSource")
            DataSource dataSource
    ) {

        Map<String, Object> jpaProperties = new HashMap<>();

        /*
         * FAS_MANAGEMENT تم إنشاؤها يدويًا.
         * Hibernate يقوم بالتحقق فقط ولا يعدل الجداول.
         */
        jpaProperties.put(
                "hibernate.hbm2ddl.auto",
                "validate"
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
                .packages("com.admin.entity")
                .persistenceUnit("managementPersistenceUnit")
                .properties(jpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager managementTransactionManager(
            @Qualifier("managementEntityManagerFactory")
            EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(
                entityManagerFactory
        );
    }
}