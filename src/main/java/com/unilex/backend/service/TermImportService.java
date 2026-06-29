package com.unilex.backend.service;

import com.unilex.backend.vo.TermImportPreviewVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 术语 Excel 导入服务
 */
public interface TermImportService {

    /**
     * 解析 Excel 文件，按 Key 路径自动解析目标目录
     * @param file 上传的 Excel 文件
     * @return 解析后的术语预览列表（含 resolvedDirPath、dirId）
     */
    List<TermImportPreviewVo> parseExcel(MultipartFile file);

    /**
     * 按路径批量导入术语（每条记录使用自身的 dirId）
     * @param list             待导入的术语列表
     * @param globalProjectName 全局所属项目名称（可选）
     * @return 成功导入的数量
     */
    int batchImportByPath(List<TermImportPreviewVo> list, String globalProjectName);

    /**
     * 兼容旧接口：批量导入到指定目录
     */
    int batchImport(Long dirId, List<TermImportPreviewVo> list);
}
