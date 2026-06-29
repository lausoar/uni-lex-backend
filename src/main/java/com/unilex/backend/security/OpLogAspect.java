package com.unilex.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unilex.backend.entity.OperationLog;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面，拦截带有 {@link OpLog} 注解的方法，自动记录操作日志。
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OpLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint point, OpLog opLog) throws Throwable {
        // 先执行业务方法
        Object result;
        String resultStatus = "UNKNOWN";
        try {
            result = point.proceed();
            resultStatus = "SUCCESS";
        } catch (Throwable ex) {
            resultStatus = "FAIL";
            throw ex;
        } finally {
            // 无论成功失败都记录日志
            try {
                saveOpLog(point, opLog, resultStatus);
            } catch (Exception e) {
                log.error("记录操作日志异常", e);
            }
        }
        return result;
    }

    private void saveOpLog(ProceedingJoinPoint point, OpLog opLog, String resultStatus) {
        // 获取当前用户信息
        Long userId = null;
        String username = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SysUser) {
            SysUser user = (SysUser) auth.getPrincipal();
            userId = user.getId();
            username = user.getUsername();
        }

        // 获取请求信息
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String url = "";
        String method = "";
        String ip = "";
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            url = request.getRequestURI();
            method = request.getMethod();
            ip = getClientIp(request);
        }

        // 获取方法参数
        String params = "";
        try {
            Object[] args = point.getArgs();
            MethodSignature sig = (MethodSignature) point.getSignature();
            String[] paramNames = sig.getParameterNames();
            if (paramNames != null && paramNames.length > 0) {
                java.util.Map<String, Object> paramMap = new java.util.LinkedHashMap<>();
                for (int i = 0; i < paramNames.length; i++) {
                    // 避免记录过大的参数（如文件流）
                    if (args[i] != null && !(args[i] instanceof javax.servlet.http.HttpServletRequest)
                            && !(args[i] instanceof javax.servlet.http.HttpServletResponse)) {
                        paramMap.put(paramNames[i], args[i]);
                    }
                }
                String json = objectMapper.writeValueAsString(paramMap);
                // 截断超长参数，最多保留 2000 字符
                params = json.length() > 2000 ? json.substring(0, 2000) + "..." : json;
            }
        } catch (Exception e) {
            params = "[参数序列化失败]";
        }

        // 构建描述
        String description = opLog.description();

        // 构建日志对象
        OperationLog logEntry = OperationLog.builder()
                .userId(userId)
                .username(username)
                .operation(opLog.operation())
                .module(opLog.module())
                .description(description)
                .method(method)
                .url(url)
                .params(params)
                .result(resultStatus)
                .ip(ip)
                .createdAt(LocalDateTime.now())
                .build();

        operationLogService.saveLog(logEntry);
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
