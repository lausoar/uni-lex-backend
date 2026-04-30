package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户分页VO，用于用户列表展示。
 */
@Data
@Builder
public class UserPageVo {
    /** 用户ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 用户状态 */
    private Integer status;
    /** 角色编码 */
    private String role;
    /** 角色中文描述 */
    private String roleDesc;
    /** 权限列表 */
    private List<String> permissions;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;
    /** 当前用户角色ID列表 */
    private List<Long>   roleIdList;   // 当前用户角色 id
    /** 当前用户权限码列表 */
    private List<String> permCodeList; // 当前用户权限码
}
