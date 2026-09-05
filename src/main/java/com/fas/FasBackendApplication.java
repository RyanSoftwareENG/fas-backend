package com.fas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FasBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FasBackendApplication.class, args);
    }
}