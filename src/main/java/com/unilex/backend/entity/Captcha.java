package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("captcha")
public class Captcha {
    private String uuid;
    private String code;
    private LocalDateTime expireTime;
}
