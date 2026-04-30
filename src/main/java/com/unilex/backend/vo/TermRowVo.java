package com.unilex.backend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;


/**
 * 术语行VO，用于术语列表展示及目录树挂载。
 */
@Data
@Builder
public class TermRowVo {
    /** 术语ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 创建人ID */
    private Long creator;
    /** 短键 */
    private String shortKey;
    /** 定义说明 */
    private String definition;
    /** 简体中文 */
    private String zhCn;
    /** 美式英文 */
    private String enUs;
    /** 日文 */
    private String jaJp;
    /** 项目名称 */
    private String projectName;
    /** SmartOM产品是否启用 */
    private Boolean productSmartom;
    /** EMS产品是否启用 */
    private Boolean productEms;
    /** OnePoint产品是否启用 */
    private Boolean productOnepoint;
    /** 是否预定义 */
    private Boolean predefined;
    /** 是否已确认 */
    private Boolean confirmed;
    /** 排序序号 */
    private Integer sortOrder;
    /** 所属目录ID */
    private Long dirId;
}
