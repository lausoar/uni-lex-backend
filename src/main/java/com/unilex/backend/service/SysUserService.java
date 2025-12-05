package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    boolean exist(String username);

    void register(String username, String rawPassword);
}
// 功能模块：用户 Service 接口