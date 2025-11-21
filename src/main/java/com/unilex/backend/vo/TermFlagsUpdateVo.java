package com.unilex.backend.vo;

import lombok.Data;

@Data
public class TermFlagsUpdateVo {
    private Boolean confirmed;      // 传 null 表示不更新该字段
    private Boolean predefined;     // 传 null 表示不更新该字段
}