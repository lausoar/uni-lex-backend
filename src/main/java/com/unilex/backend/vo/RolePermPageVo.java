package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色权限分页VO，用于角色权限管理列表展示。
 */
@Data
@Builder
public class RolePermPageVo {
    /** 角色ID */
    private Long id;
    /** 角色编码 */
    private String name;   // 角色编码
    /** 中文描述 */
    private String desc;   // 中文描述
    /** 已关联权限ID列表 */
    private List<Long> permIdList; // 已关联权限 id
    /** 创建时间 */
    private LocalDateTime createdAt;
}
