package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色Mapper，提供角色及其关联权限的查询操作。
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    /* 新增：根据用户ID查角色 */
    /**
     * 根据用户ID查询其关联的角色列表。
     */
    @Select("SELECT r.* " +
            "FROM sys_role r " +
            "JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<SysRole> listByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询其关联的权限名称列表。
     */
    @Select("SELECT p.perm_name " +
            "FROM sys_perm p " +
            "JOIN sys_role_perm rp ON p.id = rp.perm_id " +
            "WHERE rp.role_id = #{roleId}")
    List<String> listPermCodesByRoleId(@Param("roleId") Long roleId);
}
