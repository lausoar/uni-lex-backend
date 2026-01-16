package com.unilex.backend.service;

import com.unilex.backend.vo.CaptchaRecordsVo;

public interface CaptchaRecordsService {

    /**
     * 生成滑块验证码
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