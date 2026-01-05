package com.unilex.backend.vo;

import lombok.Data;
import java.util.Map;

@Data
public class TermBatchSortVo {
    // key: termId   value: 新的 sortOrder
    private Map<Long, Integer> idOrderMap;
}