package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysUser;

import java.util.List;

/**
 * 系统用户服务接口
 * <p>提供用户注册、查询、密码修改、角色权限绑定等功能</p>
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 判断用户名是否已存在
     * @param username 用户名
     * @return true-已存在
     */
    boolean exist(String username);

    /**
     * 用户注册
     * @param username 用户名
     * @param rawPassword 原始密码（明文）
     */
    void register(String username, String rawPassword);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体（不存在返回null）
     */
    SysUser getByUsername(String username);

    /**
     * 保存用户并绑定角色与权限（新增/编辑都会走到这里）
     * @param user 用户实体
     * @param roleIdList 角色ID列表
     * @param permCodeList 权限编码列表
     */
    void saveUserWithRoleAndPerm(SysUser user, List<Long> roleIdList, List<String> permCodeList);

    /**
     * 更新用户token版本号（+1），用于强制下线其他设备
     * @param username 用户名
     */
    void incrementTokenVersion(String username);

    /**
     * 获取用户当前token版本号
     * @param username 用户名
     * @return token版本号（用户不存在返回0）
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