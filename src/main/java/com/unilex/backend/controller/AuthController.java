package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.entity.SysUser;
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

    @PostMapping("/register")
    public ResponseEntity<R<Void>> register(@RequestParam String username,
                                            @RequestParam String password,
                                            @RequestParam String captchaUuid,
                                            @RequestParam String captchaCode) {
        log.info("注册请求：username={}, uuid={}", username, captchaUuid);

        if (!captchaService.validate(captchaUuid, captchaCode)) {
            return ResponseEntity.badRequest()
                    .body(R.error(400, "验证码错误 or 已过期"));
        }

        if (userService.exist(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(R.error(409, "账号已存在"));
        }

        userService.register(username, password);
        return ResponseEntity.ok(R.ok(null));
    }

    @PostMapping("/login/captcha")
    public ResponseEntity<R<LoginRespVo>> loginWithCaptcha(@RequestParam String username,
                                                           @RequestParam String password,
                                                           @RequestParam String captchaUuid,
                                                           @RequestParam String captchaCode) {
        log.info("登录请求：username={}, uuid={}", username, captchaUuid);

        //图片验证码功能
//        if (!captchaService.validate(captchaUuid, captchaCode)) {
//            return ResponseEntity.badRequest()
//                    .body(R.error(400, "验证码错误 or 已过期"));
//        }

        try {
            // 1. 验证账号密码
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // 2. 更新token版本号（关键：挤掉其他设备）
            userService.incrementTokenVersion(username);

            // 3. 获取最新的token版本号
            Integer tokenVersion = userService.getTokenVersion(username);

            // 4. 生成携带版本号的token
            String token = jwtUtil.generateToken(username, tokenVersion);

            // 5. 获取用户信息
            List<String> perms = permService.listUserPerms(username);
            SysUser user = userService.getByUsername(username);

            LoginRespVo resp = LoginRespVo.builder()
                    .token(token)
                    .username(username)
                    .perms(perms)
                    .userId(user.getId())
                    .build();

            return ResponseEntity.ok(R.ok(resp));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(R.error(401, "用户名或密码错误"));
        }
    }
}