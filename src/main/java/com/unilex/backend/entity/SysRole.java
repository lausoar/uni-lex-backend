package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统角色实体类
 */
@Data
@TableName("sys_role")
public class SysRole {
    /** 角色ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 角色英文名 */
    private String name;   // 角色英文名

    /** 中文描述 */
    @TableField("`desc`")   // <-- 关键：加反引号
    private String desc;   // 中文描述
}
