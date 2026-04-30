package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysPerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统权限Mapper，提供权限编码及名称的查询操作。
 */
@Mapper
public interface SysPermMapper extends BaseMapper<SysPerm> {

    /**
     * 根据用户名查询其拥有的所有权限编码列表。
     */
    @Select("SELECT DISTINCT p.perm_code " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.id = ur.user_id " +
            "JOIN sys_role_perm rp ON ur.role_id = rp.role_id " +
            "JOIN sys_perm p ON rp.perm_id = p.id " +
            "WHERE u.username = #{username}")
    List<String> listCodesByUsername(String username);

    /**
     * 根据用户名查询其拥有的所有权限名称列表。
     */
    @Select("SELECT DISTINCT p.perm_name " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.id = ur.user_id " +
            "JOIN sys_role_perm rp ON ur.role_id = rp.role_id " +
            "JOIN sys_perm p ON rp.perm_id = p.id " +
            "WHERE u.username = #{username}")
    List<String> listNamesByUsername(String username);
}
