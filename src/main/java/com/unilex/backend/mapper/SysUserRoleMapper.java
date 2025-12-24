package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysUserRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /** 幂等删除 */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteByUserIdAndRoleId(@Param("userId") Long userId);

    /** 单条授权 */
    @Insert("INSERT INTO sys_user_role(user_id, role_id) VALUES (#{userId}, #{permId})")
    void insertRole(@Param("userId") Long userId, @Param("permId") Long permId);

    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

}