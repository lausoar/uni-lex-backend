package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysUser;
import org.apache.ibatis.annotations.Select;
import java.util.Optional;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    Optional<SysUser> findByUsername(String username);
}
// 功能模块：用户 DAO