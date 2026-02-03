package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.dto.CaptchaVerifyDto;
import com.unilex.backend.service.CaptchaRecordsService;
import com.unilex.backend.service.CaptchaService;
import com.unilex.backend.vo.CaptchaRecordsVo;
import com.unilex.backend.vo.CaptchaVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class CaptchaController {

    private final CaptchaService captchaService;
    private final CaptchaRecordsService captchaRecordsService;


    /** 获取验证码 */
    @GetMapping("/captcha")
    public R<CaptchaVo> captcha() {
        return R.ok(captchaService.generate());
    }

    /** 生成滑块验证码 */
    @GetMapping("/captchas")
    public ResponseEntity<R<CaptchaRecordsVo>> generate() {
        CaptchaRecordsVo captcha = captchaRecordsService.generateCaptcha();
        return ResponseEntity.ok(R.ok(captcha));
    }

    /**
     * 验证滑块位置
     * @param  {uuid: string, userX: number}
     */
    @PostMapping("/captchas/verify")
    public ResponseEntity<R<Void>> verify(@RequestBody CaptchaVerifyDto dto) {
        log.info("=== 滑块验证请求 ===");
        log.info("参数: uuid={}, userX={}", dto.getUuid(), dto.getUserX());

        try {
            boolean isValid = captchaRecordsService.validate(dto.getUuid(), dto.getUserX());
            log.info("验证结果: {}", isValid ? "通过" : "失败");

            if (isValid) {
                return ResponseEntity.ok(R.ok(null));
            } else {
                return ResponseEntity.status(400)
                        .body(R.error(400, "验证失败，请重试"));
            }
        } catch (Exception e) {
            log.error("验证异常", e);
            return ResponseEntity.status(500)
                    .body(R.error(500, "验证服务异常"));
        }
    }
}