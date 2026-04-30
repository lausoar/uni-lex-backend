package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysPerm;
import java.util.List;

/**
 * 系统权限服务接口
 * <p>提供权限编码查询、用户权限校验及权限维护功能</p>
 */
public interface SysPermService extends IService<SysPerm> {

    /**
     * 查询用户拥有的权限编码列表
     * @param username 用户名
     * @return 权限编码列表
     */
    List<String> listUserPerms(String username);

    /**
     * 查询用户拥有的权限名称列表
     * @param username 用户名
     * @return 权限名称列表
     */
    List<String> listUserPermsName(String username);

    /**
     * 判断用户是否拥有指定权限
     * @param username 用户名
     * @param permCode 权限编码
     * @return true-拥有该权限
     */
    boolean hasPerm(String username, String permCode);

    /**
     * 查询全部权限
     * @return 权限实体列表
     */
    List<SysPerm> listAll();

    /**
     * 保存权限（新增或更新）
     * @param po 权限实体
     */
    void savePerm(SysPerm po);
}