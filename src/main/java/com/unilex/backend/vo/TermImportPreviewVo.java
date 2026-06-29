package com.unilex.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermImportPreviewVo {

    private Integer rowNum;
    private String shortKey;
    private String fullPath;
    private String resolvedDirPath;
    private Long dirId;
    private Boolean dirExists;
    private Boolean keyExists;
    private Long existingTermId;

    /* 产品标记（用户选择） */
    private Boolean productSmartom;
    private Boolean productEms;
    private Boolean productOnepoint;

    /* 已有术语的当前状态 */
    private Boolean existSmartom;
    private Boolean existEms;
    private Boolean existOnepoint;

    /** 已有术语的当前所属项目（用于版本对比） */
    private String existProjectName;

    /* 基础字段 */
    private String zhCn;
    private String enUs;
    private String jaJp;
    private String definition;
    private String projectName;
    private Boolean valid;
    private String reason;
}
