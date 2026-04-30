package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysUserRole;

/**
 * 用户-角色关联服务接口
 * <p>维护用户与角色的多对多关联关系</p>
 */
public interface SysUserRoleService extends IService<SysUserRole> {
}