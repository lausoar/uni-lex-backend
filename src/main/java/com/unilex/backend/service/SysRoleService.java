package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<SysRole> listAll();
    List<SysRole> listByUserId(Long userId);
}