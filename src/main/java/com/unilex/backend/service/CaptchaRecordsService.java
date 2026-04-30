package com.unilex.backend.service;

import com.unilex.backend.vo.CaptchaRecordsVo;

/**
 * 滑块验证码记录服务接口
 * <p>提供滑块验证码的生成与位置校验功能</p>
 */
public interface CaptchaRecordsService {

    /**
     * 生成滑块验证码
     * @return 滑块验证码信息（包含背景图、拼图、UUID等）
     */
    CaptchaRecordsVo generateCaptcha();

    /**
     * 验证滑块位置
     * @param uuid 验证码标识
     * @param userX 用户拖拽的位置
     * @return 是否验证通过
     */
    boolean validate(String uuid, Integer userX);
}