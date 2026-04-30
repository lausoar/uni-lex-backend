package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysRolePerm;
import com.unilex.backend.mapper.SysRolePermMapper;
import com.unilex.backend.service.SysRolePermService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 角色-权限关联服务实现
 * <p>基于 MyBatis-Plus ServiceImpl 提供基础 CRUD 能力</p>
 */
@Service
@RequiredArgsConstructor
public class SysRolePermServiceImpl extends ServiceImpl<SysRolePermMapper, SysRolePerm>
        implements SysRolePermService {
}