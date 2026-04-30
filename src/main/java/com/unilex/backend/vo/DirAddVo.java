package com.unilex.backend.vo;

import lombok.Data;

/**
 * 目录添加VO，用于新增目录时提交参数。
 */
@Data
public class DirAddVo {
    /** 目录层级：1/2/3 */
    private Integer level;      // 1 2 3
    /** 一级目录ID（level=2/3 时必传） */
    private Long    level1Id;   // 一级 id（level=2/3 时必传）
    /** 二级目录ID（level=3 时必传） */
    private Long    level2Id;   // 二级 id（level=3 时必传）
    /** 目录中文名称 */
    private String  name;       // 目录中文名称
}
