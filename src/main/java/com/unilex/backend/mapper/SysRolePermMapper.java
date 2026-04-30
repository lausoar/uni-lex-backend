package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.SysRolePerm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper，提供角色与权限关联关系的CRUD操作。
 */
@Mapper
public interface SysRolePermMapper extends BaseMapper<SysRolePerm> {
}
