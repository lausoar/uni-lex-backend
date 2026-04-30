package com.unilex.backend.vo;

import lombok.Data;
import java.util.Map;

/**
 * 术语批量排序VO，用于批量更新术语排序序号。
 */
@Data
public class TermBatchSortVo {
    // key: termId   value: 新的 sortOrder
    /** 术语ID与排序序号的映射，key为termId，value为新的sortOrder */
    private Map<Long, Integer> idOrderMap;
}
