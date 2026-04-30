package com.unilex.backend.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 认证失败处理器，当未认证用户访问受保护资源时触发。
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * 处理认证失败请求，返回 401 状态码及提示信息。
     *
     * @param request       HTTP 请求
     * @param response      HTTP 响应
     * @param authException 认证异常
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 设置 401 未授权状态码
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 设置响应内容类型为 JSON
        response.setContentType("application/json;charset=UTF-8");
        // 设置自定义响应头，标识 Token 已过期
        response.setHeader("X-Token-Expired", "true");
        // 写入 JSON 格式的错误响应体
        response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
    }
}
// 功能模块：JWT 认证失败处理器
