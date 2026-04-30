package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 滑块验证码记录VO，用于封装验证码图片及拼图相关信息。
 */
@Data
@Builder
public class CaptchaRecordsVo {
    /** 验证码唯一标识 */
    private String uuid;
    /** 背景图Base64 */
    private String backgroundImage;
    /** 拼图块Base64 */
    private String puzzleImage;
    /** 拼图块宽度 */
    private Integer puzzleWidth;
    /** 拼图块高度 */
    private Integer puzzleHeight;
    /** 拼图在背景图上的真实X坐标 */
    private Integer puzzleX;  // 新增：拼图在背景图上的真实位置
}
