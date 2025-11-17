package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cat_directory")
public class CatDirectory {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long parentId;
    private Integer dirType;
    private String dirKey;
    private String dirNameZh;
    private String dirNameEn;
    private Integer sortOrder;
    private Integer isSystem;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
