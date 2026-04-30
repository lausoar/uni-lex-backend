package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysRole;

import java.util.List;

/**
 * 系统角色服务接口
 * <p>提供角色查询、用户角色关联及角色权限绑定功能</p>
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 查询全部角色
     * @return 角色实体列表
     */
    List<SysRole> listAll();

    /**
     * 查询指定用户拥有的角色
     * @param userId 用户ID
     * @return 角色实体列表
     */
    List<SysRole> listByUserId(Long userId);

    /**
     * 保存角色并关联权限
     * @param role 角色实体
     * @param permIdList 权限ID列表
     */
    void saveRoleWithPerms(SysRole role, List<Long> permIdList);
}