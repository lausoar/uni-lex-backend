package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 权限分页VO，用于权限列表展示。
 */
@Data
@Builder
public class PermPageVo {
    /** 权限ID */
    private Integer id;
    /** 权限编码 */
    private String permCode;
    /** 权限名称 */
    private String permName;
    /** 是否系统内置 */
    private Boolean isSystem;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
