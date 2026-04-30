package com.unilex.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类，用于配置全局 CORS 策略。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 添加跨域映射规则，允许所有来源、方法和请求头。
     *
     * @param registry Cors 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置全局跨域：允许所有路径、来源、方法、请求头，并允许携带凭证
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
