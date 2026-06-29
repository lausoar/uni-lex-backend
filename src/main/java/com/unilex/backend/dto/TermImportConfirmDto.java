package com.unilex.backend.dto;

import com.unilex.backend.vo.TermImportPreviewVo;
import lombok.Data;

import java.util.List;

/**
 * Excel 导入确认请求 DTO
 */
@Data
public class TermImportConfirmDto {

    /** 目标目录 ID（兼容旧接口，可传 null） */
    private Long dirId;

    /** 全局所属项目名称（会应用到所有未单独指定项目的术语） */
    private String projectName;

    /** 待导入的术语列表 */
    private List<TermImportPreviewVo> terms;
}
