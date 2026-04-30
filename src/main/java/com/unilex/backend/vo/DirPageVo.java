package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 目录分页VO，用于目录列表展示。
 */
@Data
@Builder
public class DirPageVo {
    /** 目录ID */
    private Long id;
    /** 父目录ID */
    private Long parentId;
    /** 目录类型：1/2/3 */
    private Integer dirType;
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
    /** 创建时间 */
    private LocalDateTime createdAt;
}
