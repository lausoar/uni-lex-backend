package com.unilex.backend.dto;

import lombok.Data;

/**
 * 验证码校验DTO
 */
@Data
public class CaptchaVerifyDto {
    /** 验证码唯一标识 */
    private String uuid;
    /** 用户滑块X坐标 */
    private Integer userX;
}
