package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 术语词条实体类
 */
@Data
@TableName("term_entry")
public class TermEntry {

    /** 词条ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属目录ID */
    private Long dirId;
    /** 创建者ID */
    @TableField("creator")
    private Long creator;
    /** 短键名 */
    private String shortKey;
    /** 定义说明 */
    private String definition;
    /** 简体中文翻译 */
    private String zhCn;
    /** 美式英语翻译 */
    private String enUs;
    /** 日语翻译 */
    private String jaJp;
    /** 项目名称 */
    private String projectName;
    /** 是否关联SmartOM产品：0-否，1-是 */
    private Integer productSmartom;
    /** 是否关联EMS产品：0-否，1-是 */
    private Integer productEms;
    /** 是否关联OnePoint产品：0-否，1-是 */
    private Integer productOnepoint;
    /** 是否预定义：0-否，1-是 */
    private Integer isPredefined;
    /** 是否已确认：0-否，1-是 */
    private Integer confirmed;
    /** 排序号 */
    private Integer sortOrder;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
