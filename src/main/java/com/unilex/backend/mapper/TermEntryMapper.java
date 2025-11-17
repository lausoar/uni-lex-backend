package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.entity.TermEntry;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TermEntryMapper extends BaseMapper<TermEntry> {

    @Select("SELECT id, short_key, definition, zh_cn, en_us, ja_jp, " +
            "       product_smartom, product_ems, product_onepoint, " +
            "       is_predefined, confirmed, sort_order " +
            "FROM   term_entry " +
            "WHERE  dir_id = #{dirId} " +
            "ORDER  BY sort_order ASC, id ASC")
    IPage<TermEntry> pageByDir(Page<TermEntry> page, @Param("dirId") Long dirId);
}
