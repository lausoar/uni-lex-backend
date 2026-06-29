package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("term_version_log")
public class TermVersionLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long termId;
    private Long dirId;
    private String shortKey;
    private String oldVersion;
    private String newVersion;
    private String changeType;
    private Long operatorId;
    private String operatorName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
