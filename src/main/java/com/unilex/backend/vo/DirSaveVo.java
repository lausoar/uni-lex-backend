package com.unilex.backend.vo;

import lombok.Data;

/**
 * 目录保存VO，用于编辑或新增目录时提交参数。
 */
@Data
public class DirSaveVo {
    /** 目录ID，有值表示编辑 */
    private Long id;        // 有值=编辑
    /** 父目录ID */
    private Long parentId;
    /** 目录类型：1/2/3 */
    private Integer dirType;   // 1/2/3
    /** 目录编码 */
    private String dirKey;
    /** 目录中文名 */
    private String dirNameZh;
    /** 目录英文名 */
    private String dirNameEn;
    /** 排序序号 */
    private Integer sortOrder;
    /** 是否系统内置 */
    private Boolean isSystem;
}
