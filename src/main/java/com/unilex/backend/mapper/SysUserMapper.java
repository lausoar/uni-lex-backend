package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysUser;
import org.apache.ibatis.annotations.Select;
import java.util.Optional;

/**
 * 系统用户Mapper，提供用户查询操作。
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户。
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    Optional<SysUser> findByUsername(String username);
}
// 功能模块：用户 DAO
