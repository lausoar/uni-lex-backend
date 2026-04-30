package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色分页VO，用于角色列表展示。
 */
@Data
@Builder
public class RolePageVo {
    /** 角色ID */
    private Long id;
    /** 角色编码，如 ROLE_ADMIN */
    private String name;   // 角色编码，如 ROLE_ADMIN
    /** 中文描述 */
    private String desc;   // 中文描述
    /** 权限编码集合 */
    private List<String> permList; // 权限编码集合
    /** 创建时间 */
    private LocalDateTime createdAt;
}
