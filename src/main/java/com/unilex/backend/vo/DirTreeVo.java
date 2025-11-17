package com.unilex.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class DirTreeVo {
    private Long id;
    private Long parentId;
    private String dirKey;
    private String name;   // 根据 Accept-Language 放 zh/en
    private Integer dirType;
    private Integer sortOrder;
    private Integer isSystem;
    private List<DirTreeVo> children; // 一级二级三级都通用
    private List<TermRowVo> termList;   // ✅ 叶子目录挂术语
}