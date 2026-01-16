package com.unilex.backend.dto;

import lombok.Data;

@Data
public class CaptchaVerifyDto {
    private String uuid;
    private Integer userX;
}