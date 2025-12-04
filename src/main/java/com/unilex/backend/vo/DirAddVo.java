package com.unilex.backend.vo;

import lombok.Data;

@Data
public class DirAddVo {
    private Integer level;      // 1 2 3
    private Long    level1Id;   // 一级 id（level=2/3 时必传）
    private Long    level2Id;   // 二级 id（level=3 时必传）
    private String  name;       // 目录中文名称
}