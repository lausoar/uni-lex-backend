package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.CatDirectory;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 目录Mapper，提供目录树的查询操作。
 */
public interface CatDirectoryMapper extends BaseMapper<CatDirectory> {

    /**
     * 一次性查询全部目录并按父ID、排序号排序，用于内存拼装树形结构。
     */
    // 一次性把整棵树抓出来，内存里拼父子（数据量 <1w 时最简单）
    @Select("SELECT id, parent_id, dir_type, dir_key, dir_name_zh, dir_name_en, sort_order, is_system FROM cat_directory ORDER BY parent_id, sort_order")
    List<CatDirectory> listAllOrdered();
}
