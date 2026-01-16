package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaRecordsVo {
    private String uuid;
    private String backgroundImage;
    private String puzzleImage;
    private Integer puzzleWidth;
    private Integer puzzleHeight;
    private Integer puzzleX;  // 新增：拼图在背景图上的真实位置
}