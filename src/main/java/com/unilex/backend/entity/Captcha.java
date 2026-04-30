package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 验证码实体类，用于存储验证码信息
 */
@Data
@TableName("captcha")
public class Captcha {
    /** 验证码唯一标识 */
    private String uuid;
    /** 验证码内容 */
    private String code;
    /** 验证码过期时间 */
    private LocalDateTime expireTime;
}
