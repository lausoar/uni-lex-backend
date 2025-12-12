package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.mapper.SysRoleMapper;
import com.unilex.backend.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    // 新增：查全部角色
    public List<SysRole> listAll() {
        return list();
    }

    @Override
    public List<SysRole> listByUserId(Long userId) {
        return getBaseMapper().listByUserId(userId);
    }
}