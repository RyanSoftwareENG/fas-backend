package com.admin.config;

import com.admin.security.AdminPermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminPermissionInterceptor
            adminPermissionInterceptor;

    public WebConfig(
            AdminPermissionInterceptor adminPermissionInterceptor
    ) {
        this.adminPermissionInterceptor =
                adminPermissionInterceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry
    ) {

        registry.addInterceptor(
                adminPermissionInterceptor
        );
    }
}