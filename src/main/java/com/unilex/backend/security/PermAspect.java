package com.unilex.backend.security;

import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.TermService;
import com.unilex.backend.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
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

@Aspect
@Component
@RequiredArgsConstructor
public class PermAspect {

    private final SysPermService permService;
    private final TermService termService;   // 用于查 creator

    @Before("@annotation(reqPerm)")
    public void check(ReqPerm reqPerm) {
        UserDetails user = (UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        String code = reqPerm.value();

        /* 1. 有全局权限直接过 */
        if (permService.hasPerm(username, code)) {
            return;
        }

        /* 2. 如果是 term:update / term:delete 再判断“自己创建” */
        if ("term:update".equals(code) || "term:delete".equals(code)) {
            Long userId = SecurityUtil.currentUserId();   // 当前登录人ID
            if (userId == null) throw new SecurityException("无权限：" + code);

            // 从 URL 里取术语 ID
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