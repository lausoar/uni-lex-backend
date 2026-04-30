package com.unilex.backend.vo;

import lombok.Data;

/**
 * 术语状态批量更新VO，用于批量修改术语确认及预定义状态。
 */
@Data
public class TermFlagsUpdateVo {
    /** 是否已确认，传null表示不更新该字段 */
    private Boolean confirmed;      // 传 null 表示不更新该字段
    /** 是否预定义，传null表示不更新该字段 */
    private Boolean predefined;     // 传 null 表示不更新该字段
}
