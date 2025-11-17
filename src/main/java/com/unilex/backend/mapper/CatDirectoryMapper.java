package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.CatDirectory;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CatDirectoryMapper extends BaseMapper<CatDirectory> {

    // 一次性把整棵树抓出来，内存里拼父子（数据量 <1w 时最简单）
    @Select("SELECT * FROM cat_directory ORDER BY parent_id, sort_order")
    List<CatDirectory> listAllOrdered();
}
