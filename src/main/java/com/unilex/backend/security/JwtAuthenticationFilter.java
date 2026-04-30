package com.unilex.backend.security;

import com.unilex.backend.service.SysUserService;
import com.unilex.backend.utils.JwtUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器，拦截请求并验证 JWT Token 的有效性。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT 工具类，用于 Token 的解析与校验 */
    private final JwtUtil jwtUtil;
    /** 用户详情服务，用于加载用户信息 */
    private final UserDetailsService userDetailsService;
    /** 系统用户服务，用于验证 Token 版本 */
    private final SysUserService userService; // 用于验证token版本

    /**
     * 执行过滤器内部逻辑，校验请求头中的 JWT Token。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param chain    过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // 从请求头中获取 Authorization
        String header = request.getHeader("Authorization");

        // 若请求头为空或不以 Bearer 开头，则直接放行
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // 截取 Bearer 后的 Token 字符串
        String token = header.substring(7);

        // 校验 Token 是否有效
        if (jwtUtil.validate(token)) {
            // 从 Token 中解析用户名和 Token 版本号
            String username = jwtUtil.getUsername(token);
            Integer tokenVersionInToken = jwtUtil.getTokenVersion(token);
            // 从数据库获取当前用户的最新 Token 版本号
            Integer currentTokenVersion = userService.getTokenVersion(username);

            // 校验 Token 版本号是否与数据库一致（防止 Token 被踢下线后仍可使用）
            if (tokenVersionInToken != null && tokenVersionInToken.equals(currentTokenVersion)) {
                // 加载用户信息并构建认证对象
                UserDetails user = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                // 设置请求详情
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息存入 Security 上下文
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // 关键：设置自定义 header，标识版本不匹配
                response.setHeader("X-Token-Version-Mismatch", "true");
                log.warn("Token version mismatch for user: {}", username);
            }
        }

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
}
