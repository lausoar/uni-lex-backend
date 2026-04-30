package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysUserRole;
import com.unilex.backend.mapper.SysUserRoleMapper;
import com.unilex.backend.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户-角色关联服务实现
 * <p>基于 MyBatis-Plus ServiceImpl 提供基础 CRUD 能力</p>
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {
}