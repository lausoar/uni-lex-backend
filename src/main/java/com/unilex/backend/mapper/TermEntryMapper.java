package com.unilex.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unilex.backend.entity.TermEntry;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 术语条目Mapper，提供术语排序更新及批量查询操作。
 */
public interface TermEntryMapper extends BaseMapper<TermEntry> {

    /**
     * 批量更新术语排序序号。
     */
    @Update(
            "<script>" +
                    "UPDATE term_entry " +
                    "SET sort_order = CASE id " +
                    " <foreach collection='idOrderMap' item='sortOrder' index='id'>" +
                    "   WHEN #{id} THEN #{sortOrder} " +
                    " </foreach>" +
                    " ELSE sort_order " +
                    "END, " +
                    "updated_at = #{now} " +
                    "WHERE id IN " +
                    " <foreach collection='idOrderMap.keys' item='id' open='(' separator=',' close=')'>" +
                    "   #{id} " +
                    " </foreach>" +
                    "</script>"
    )
    int batchUpdateSort(@Param("idOrderMap") Map<Long, Integer> idOrderMap,
                        @Param("now") LocalDateTime now);

    /**
     * 批量查询指定ID集合的术语排序信息。
     */
    @Select(
            "<script>" +
                    "SELECT id, sort_order " +
                    "FROM term_entry " +
                    "WHERE id IN " +
                    " <foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
                    "   #{id} " +
                    " </foreach>" +
                    "</script>"
    )
    List<TermEntry> selectBatchSorts(@Param("ids") Set<Long> ids);
}

