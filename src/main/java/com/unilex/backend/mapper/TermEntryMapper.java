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

public interface TermEntryMapper extends BaseMapper<TermEntry> {
    /**
     * 批量更新排序（CASE WHEN 方案，单条 SQL）
     */
    @Update("""
        <script>
        UPDATE term_entry
        SET sort_order = CASE id
            <foreach collection="idOrderMap" item="sortOrder" index="id">
                WHEN #{id} THEN #{sortOrder}
            </foreach>
            ELSE sort_order
        END,
        updated_at = #{now}
        WHERE id IN
        <foreach collection="idOrderMap.keys" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
        </script>
    """)
    int batchUpdateSort(@Param("idOrderMap") Map<Long, Integer> idOrderMap,
                        @Param("now") LocalDateTime now);

    /**
     * 批量查询最新的排序值（IN 查询）
     */
    @Select("""
        <script>
        SELECT id, sort_order 
        FROM term_entry 
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
        </script>
    """)
    List<TermEntry> selectBatchSorts(@Param("ids") Set<Long> ids);
}
