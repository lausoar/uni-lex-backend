package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    boolean exist(String username);

    void register(String username, String rawPassword);

    SysUser getByUsername(String username);
    /* 新增/编辑都会走到这里 */
    void saveUserWithRoleAndPerm(SysUser user, List<Long> roleIdList, List<String> permCodeList);

    /**
     * 更新用户token版本号（+1）
     */
    void incrementTokenVersion(String username);

    /**
     * 获取用户当前token版本号
     */
    Integer getTokenVersion(String username);
}
// 功能模块：用户 Service 接口