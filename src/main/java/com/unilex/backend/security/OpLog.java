package com.unilex.backend.security;

import java.lang.annotation.*;

/**
 * 操作日志注解，标注在 Controller 方法上，自动记录操作日志。
 *
 * 使用示例：
 * <pre>
 *   @OpLog(module = "term", operation = "CREATE", description = "新增术语")
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {

    /** 操作模块，如 term / directory */
    String module();

    /** 操作类型，如 CREATE / UPDATE / DELETE */
    String operation();

    /** 操作描述 */
    String description() default "";
}
