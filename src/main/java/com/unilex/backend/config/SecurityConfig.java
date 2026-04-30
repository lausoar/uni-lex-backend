package com.unilex.backend.config;

import com.unilex.backend.security.JwtAuthenticationEntryPoint;
import com.unilex.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类，配置安全过滤链、密码编码器和认证管理器。
 */
@Configuration
public class SecurityConfig {

    /** JWT 认证失败处理器 */
    private final JwtAuthenticationEntryPoint entryPoint;
    /** JWT 认证过滤器 */
    private final JwtAuthenticationFilter jwtFilter;

    /**
     * 构造方法，通过构造函数注入依赖。
     *
     * @param entryPoint JWT 认证失败处理器
     * @param jwtFilter  JWT 认证过滤器（懒加载避免循环依赖）
     */
    // 使用构造函数注入 + @Lazy
    public SecurityConfig(
            JwtAuthenticationEntryPoint entryPoint,
            @Lazy JwtAuthenticationFilter jwtFilter) {
        this.entryPoint = entryPoint;
        this.jwtFilter = jwtFilter;
    }

    /**
     * 密码编码器，使用 BCrypt 强哈希算法。
     *
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器，从 Spring Security 配置中获取。
     *
     * @param config 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 获取失败时抛出异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤链配置，定义请求授权规则、会话管理及异常处理。
     *
     * @param http HttpSecurity 配置对象
     * @return SecurityFilterChain 实例
     * @throws Exception 配置失败时抛出异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 关闭 CSRF 防护（前后端分离项目通常使用 JWT）
        http
                .csrf().disable()
                // 无状态会话管理，不使用 Session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置请求授权规则
                .authorizeRequests()
                // 认证相关接口允许匿名访问
                .antMatchers(
                        "/api/auth/captchas",
                        "/api/auth/login"
                ).permitAll()
                // GET 请求获取验证码允许匿名访问
                .antMatchers(HttpMethod.GET, "/api/auth/captchas").permitAll()
                // auth 模块下所有接口允许匿名访问
                .antMatchers("/api/auth/**").permitAll()
                // user 模块下所有接口允许匿名访问
                .antMatchers("/api/user/**").permitAll()
                // 自动开通接口允许匿名访问
                .antMatchers("/api/auto-provision").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
                .and()
                // 配置认证异常处理器
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and()
                // 在用户名密码认证过滤器之前添加 JWT 过滤器
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 关闭 csrf
//                .csrf().disable()
//
//                // 不使用 session
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//
//                // 所有请求全部放行
//                .authorizeRequests()
//                .anyRequest().permitAll()
//
//                // 仍然可以保留 jwtFilter（但它不会拦）
//                .and()
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }


}
