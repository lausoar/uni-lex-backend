package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysRolePerm;

/**
 * 角色-权限关联服务接口
 * <p>维护角色与权限的多对多关联关系</p>
 */
public interface SysRolePermService extends IService<SysRolePerm> {
}