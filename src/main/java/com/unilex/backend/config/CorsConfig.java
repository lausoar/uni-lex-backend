package com.unilex.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类，用于配置全局 CORS 策略。
 *
 * 安全说明：不再使用通配符来源。允许的来源由配置项 cors.allowed-origins
 * 控制（逗号分隔，可用环境变量 CORS_ALLOWED_ORIGINS 覆盖），
 * 生产环境务必配置为实际前端域名。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /** 允许跨域访问的前端来源（逗号分隔） */
    @Value("${cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
    private String allowedOrigins;

    /**
     * 添加跨域映射规则：仅 API 路径、仅白名单来源、仅实际用到的方法。
     *
     * @param registry Cors 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Provision-Token")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
