package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysPerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermMapper extends BaseMapper<SysPerm> {

    @Select("SELECT DISTINCT p.perm_name " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.id = ur.user_id " +
            "JOIN sys_role_perm rp ON ur.role_id = rp.role_id " +
            "JOIN sys_perm p ON rp.perm_id = p.id " +
            "WHERE u.username = #{username}")
    List<String> listCodesByUsername(String username);
}