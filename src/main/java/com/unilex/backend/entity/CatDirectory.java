package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 目录分类实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("cat_directory")
public class CatDirectory {

    /** 目录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父目录ID */
    private Long parentId;
    /** 目录类型 */
    private Integer dirType;
    /** 目录关键字 */
    private String dirKey;
    /** 目录中文名称 */
    private String dirNameZh;
    /** 目录英文名称 */
    private String dirNameEn;
    /** 排序号 */
    private Integer sortOrder;
    /** 是否为系统内置：0-否，1-是 */
    private Integer isSystem;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
