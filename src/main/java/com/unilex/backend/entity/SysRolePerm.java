package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("sys_role_perm")
public class SysRolePerm {
    private Long roleId;
    private Long permId;
}
