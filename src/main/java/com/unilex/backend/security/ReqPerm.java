package com.unilex.backend.security;

import java.lang.annotation.*;

/**
 * 自定义权限注解，用于标注需要权限校验的方法或类。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReqPerm {
    /**
     * 权限编码。
     *
     * @return 权限编码字符串
     */
    String value();   // 权限编码
}
