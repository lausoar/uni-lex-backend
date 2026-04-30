package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 角色权限关联实体类
 */
@Data
@AllArgsConstructor
@TableName("sys_role_perm")
public class SysRolePerm {
    /** 角色ID */
    private Long roleId;
    /** 权限ID */
    private Long permId;
}
