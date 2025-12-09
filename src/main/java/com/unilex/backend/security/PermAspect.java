package com.unilex.backend.security;

import com.unilex.backend.service.SysPermService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermAspect {

    private final SysPermService permService;

    @Before("@annotation(reqPerm)")
    public void check(ReqPerm reqPerm) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        boolean ok = permService.hasPerm(user.getUsername(), reqPerm.value());
        if (!ok) throw new SecurityException("无权限：" + reqPerm.value());
    }
}