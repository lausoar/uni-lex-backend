package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 验证码记录实体类，用于存储验证码验证记录
 */
@Data
@TableName("captcha_records")
public class CaptchaRecord {
    /** 记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 验证码唯一标识 */
    private String uuid;
    /** 滑块拼图X坐标 */
    private Integer puzzleX;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 验证状态：0-未验证，1-已验证 */
    private Integer validated;
}
