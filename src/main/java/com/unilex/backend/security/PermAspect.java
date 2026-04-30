package com.unilex.backend.security;

import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.TermService;
import com.unilex.backend.utils.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * 权限校验切面，基于注解 {@link ReqPerm} 进行方法级权限控制。
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermAspect {

    /** 权限服务，用于校验全局权限 */
    private final SysPermService permService;
    /** 术语服务，用于查询术语创建者 */
    private final TermService termService;   // 用于查 creator

    /**
     * 在目标方法执行前进行权限校验。
     *
     * @param reqPerm 权限注解，包含权限编码
     */
    @Before("@annotation(reqPerm)")
    public void check(ReqPerm reqPerm) {
        // 从 Security 上下文中获取当前登录用户
        UserDetails user = (UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        // 获取注解中定义的权限编码
        String code = reqPerm.value();

        /* 1. 有全局权限直接过 */
        if (permService.hasPerm(username, code)) {
            return;
        }

        /* 2. 如果是 term:update / term:delete 再判断“自己创建” */
        if ("term:update".equals(code) || "term:delete".equals(code)) {
            Long userId = SecurityUtil.currentUserId();   // 当前登录人ID
            if (userId == null) throw new SecurityException("无权限：" + code);

            // 从 URL 或请求参数中获取术语 ID
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest req = attrs.getRequest();
            String idStr = null;

            // ① 先试 /api/term/{id} 风格
            Map<String, String> uriVars =
                    new AntPathMatcher().extractUriTemplateVariables("/api/term/{id}", req.getRequestURI());
            if (uriVars != null) idStr = uriVars.get("id");

            // ② 再试 ?id=xx 风格
            if (idStr == null) idStr = req.getParameter("id");

            if (idStr != null) {
                // 根据 ID 查询术语条目，并校验是否为当前用户创建
                TermEntry entry = termService.getById(Long.valueOf(idStr));
                if (entry != null && userId.equals(entry.getCreator())) {
                    return; // 自己创建，放行
                }
            }
        }

        /* 3. 真没权限 */
        throw new SecurityException("无权限：" + code);
    }
}
