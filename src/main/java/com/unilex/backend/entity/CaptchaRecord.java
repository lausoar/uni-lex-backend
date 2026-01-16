package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("captcha_records")
public class CaptchaRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String uuid;
    private Integer puzzleX;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private Integer validated;
}
