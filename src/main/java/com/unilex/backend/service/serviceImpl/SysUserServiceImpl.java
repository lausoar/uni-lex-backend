package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.mapper.SysUserMapper;
import com.unilex.backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean exist(String username) {
        return lambdaQuery().eq(SysUser::getUsername, username).count() > 0;
    }

    @Override
    @Transactional   // 建议加事务
    public void register(String username, String rawPassword) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        save(user);
    }
}
// 功能模块：用户 Service 实现
