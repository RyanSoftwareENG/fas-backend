package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.fas",
                "com.admin"
        }
)
public class FasBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                FasBackendApplication.class,
                args
        );
    }
}