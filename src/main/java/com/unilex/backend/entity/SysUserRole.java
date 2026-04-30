package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户角色关联实体类
 */
@Data
@AllArgsConstructor
@TableName("sys_user_role")
public class SysUserRole {
    /** 用户ID */
    private Long userId;
    /** 角色ID */
    private Long roleId;
}
