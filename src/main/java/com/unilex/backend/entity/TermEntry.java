package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("term_entry")
public class TermEntry {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dirId;
    private String shortKey;
    private String definition;
    private String zhCn;
    private String enUs;
    private String jaJp;
    private Integer productSmartom;
    private Integer productEms;
    private Integer productOnepoint;
    private Integer isPredefined;
    private Integer confirmed;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
