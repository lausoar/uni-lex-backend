package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.PermApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限申请Mapper，提供权限申请记录的CRUD操作。
 */
@Mapper
public interface PermApplyMapper extends BaseMapper<PermApply> {
}
