package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.service.CaptchaService;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.utils.JwtUtil;
import com.unilex.backend.vo.LoginRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SysUserService userService;
    private final CaptchaService captchaService;
    private final SysPermService permService;

    /* ===========================================
       注册：先校验验证码 -> 再检查账号是否存在 -> 创建用户
       =========================================== */
    @PostMapping("/register")
    public ResponseEntity<R<Void>> register(@RequestParam String username,
                                            @RequestParam String password,
                                            @RequestParam String captchaUuid,
                                            @RequestParam String captchaCode) {
        log.info("注册请求：username={}, uuid={}", username, captchaUuid);

        // 1. 验证码校验：失败直接 400
        if (!captchaService.validate(captchaUuid, captchaCode)) {
            return ResponseEntity.badRequest()
                    .body(R.error(400, "验证码错误 or 已过期"));
        }

        // 2. 账号是否存在：冲突 409
        if (userService.exist(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(R.error(409, "账号已存在"));
        }

        // 3. 创建用户
        userService.register(username, password);
        return ResponseEntity.ok(R.ok(null));
    }

    /* ===========================================
       登录：先校验验证码 -> 再校验账号密码
       =========================================== */
    @PostMapping("/login/captcha")
    public ResponseEntity<R<LoginRespVo>> loginWithCaptcha(@RequestParam String username,
                                                           @RequestParam String password,
                                                           @RequestParam String captchaUuid,
                                                           @RequestParam String captchaCode) {
        log.info("登录请求：username={}, uuid={}", username, captchaUuid);

        // 1. 验证码校验：失败 400
        if (!captchaService.validate(captchaUuid, captchaCode)) {
            return ResponseEntity.badRequest()
                    .body(R.error(400, "验证码错误 or 已过期"));
        }

        // 2. 账号密码认证
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            String token = jwtUtil.generateToken(username);
            List<String> perms = permService.listUserPerms(username);
            LoginRespVo resp = LoginRespVo.builder()
                    .token(token)
                    .username(username)
                    .perms(perms)
                    .build();
            return ResponseEntity.ok(R.ok(resp));
        } catch (BadCredentialsException e) {
            // 密码错误 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(R.error(401, "用户名或密码错误"));
        }
    }
}