package com.fas.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ClientSecurityConfig
        implements WebMvcConfigurer {

    private final ClientPermissionInterceptor
            clientPermissionInterceptor;

    public ClientSecurityConfig(
            ClientPermissionInterceptor clientPermissionInterceptor
    ) {
        this.clientPermissionInterceptor =
                clientPermissionInterceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry
    ) {

        registry.addInterceptor(
                clientPermissionInterceptor
        );
    }
}