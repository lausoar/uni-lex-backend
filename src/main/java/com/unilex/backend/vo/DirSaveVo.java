package com.unilex.backend.vo;

import lombok.Data;

@Data
public class DirSaveVo {
    private Long id;        // 有值=编辑
    private Long parentId;
    private Integer dirType;   // 1/2/3
    private String dirKey;
    private String dirNameZh;
    private String dirNameEn;
    private Integer sortOrder;
    private Boolean isSystem;
}