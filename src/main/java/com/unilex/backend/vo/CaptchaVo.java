package com.unilex.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 验证码VO，用于前端展示及后续校验。
 */
@Data
@AllArgsConstructor
public class CaptchaVo {
    /** 验证码唯一标识，前端凭此校验 */
    private String uuid;   // 前端凭此校验
    /** 验证码图片Base64 */
    private String img;    // base64 图片
}
