package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.service.CaptchaService;
import com.unilex.backend.vo.CaptchaVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /** 获取验证码 */
    @GetMapping("/captcha")
    public R<CaptchaVo> captcha() {
        return R.ok(captchaService.generate());
    }
}