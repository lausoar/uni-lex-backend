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

    /**
     * 用户自己修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws IllegalArgumentException 当旧密码错误或新密码不符合要求时
     */
    void editPassword(String username, String oldPassword, String newPassword);
}