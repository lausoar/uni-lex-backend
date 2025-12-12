package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("sys_user_role")
public class SysUserRole {
    private Long userId;
    private Long roleId;
}
