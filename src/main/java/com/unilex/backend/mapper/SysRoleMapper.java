package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    /* 新增：根据用户ID查角色 */
    @Select("SELECT r.* " +
            "FROM sys_role r " +
            "JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<SysRole> listByUserId(@Param("userId") Long userId);

    @Select("SELECT p.perm_name " +
            "FROM sys_perm p " +
            "JOIN sys_role_perm rp ON p.id = rp.perm_id " +
            "WHERE rp.role_id = #{roleId}")
    List<String> listPermCodesByRoleId(@Param("roleId") Long roleId);
}