package com.unilex.backend.vo;

import lombok.Data;

import java.util.List;

/**
 * 目录树VO，用于构建层级目录结构及挂载术语。
 */
@Data
public class DirTreeVo {
    /** 目录ID */
    private Long id;
    /** 父目录ID */
    private Long parentId;
    /** 目录编码 */
    private String dirKey;
    /** 目录名称，根据 Accept-Language 返回 zh/en */
    private String name;   // 根据 Accept-Language 放 zh/en
    /** 目录类型：1/2/3 */
    private Integer dirType;
    /** 排序序号 */
    private Integer sortOrder;
    /** 是否系统内置 */
    private Integer isSystem;
    /** 子目录列表，一级二级三级通用 */
    private List<DirTreeVo> children; // 一级二级三级都通用
    /** 叶子目录挂载的术语列表 */
    private List<TermRowVo> termList;   // ✅ 叶子目录挂术语
}
