package com.unilex.backend.service;

import com.unilex.backend.vo.CaptchaVo;

/**
 * 图形验证码服务接口
 * <p>提供普通图形验证码的生成与校验功能</p>
 */
public interface CaptchaService {

    /**
     * 生成图形验证码
     * @return 验证码信息（包含UUID与Base64图片）
     */
    CaptchaVo generate();

    /**
     * 校验用户输入的验证码
     * @param uuid 验证码标识
     * @param userInput 用户输入的验证码内容
     * @return 校验是否通过
     */
    boolean validate(String uuid, String userInput);
}
