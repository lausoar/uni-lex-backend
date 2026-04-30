package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统权限实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPerm {
    /** 权限ID */
    @TableId(type = IdType.AUTO)
    private  Integer id;
    /** 权限编码 */
    private  String permCode;
    /** 权限名称 */
    private  String permName;
    /** 是否为系统内置：0-否，1-是 */
    private  String isSystem;
    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
