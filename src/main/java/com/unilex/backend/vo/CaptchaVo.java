package com.unilex.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaVo {
    private String uuid;   // 前端凭此校验
    private String img;    // base64 图片
}
