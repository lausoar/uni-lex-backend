package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SysUserService userService;

    @PostMapping("/register")
    public R<Void> register(@RequestParam String username,
                            @RequestParam String password) {
        if (userService.exist(username)) {
            return R.error(409, "账号已存在");
        }
        userService.register(username, password);
        return R.ok(null);
    }

    @PostMapping("/login")
    public R<String> login(@RequestParam String username,
                           @RequestParam String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        String token = jwtUtil.generateToken(auth.getName());
        return R.ok(token);
    }
}
// 功能模块：认证入口