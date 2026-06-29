package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.dto.TermImportConfirmDto;
import com.unilex.backend.security.OpLog;
import com.unilex.backend.security.ReqPerm;
import com.unilex.backend.service.TermImportService;
import com.unilex.backend.vo.TermImportPreviewVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 术语 Excel 导入控制器
 */
@RestController
@RequestMapping("/api/term/import")
@RequiredArgsConstructor
public class TermImportController {

    private final TermImportService termImportService;

    /**
     * 上传 Excel 并解析预览（自动按 Key 路径解析目标目录）
     */
    @PostMapping("/preview")
    @ReqPerm("term:add")
    public R<List<TermImportPreviewVo>> preview(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error(400, "文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return R.error(400, "仅支持 .xlsx / .xls 格式的 Excel 文件");
        }
        List<TermImportPreviewVo> list = termImportService.parseExcel(file);
        return R.ok(list);
    }

    /**
     * 确认批量导入（按 Key 路径导入到对应目录，支持所属项目）
     */
    @PostMapping("/confirm")
    @ReqPerm("term:add")
    @OpLog(module = "term", operation = "CREATE", description = "Excel批量导入术语")
    public R<Map<String, Object>> confirm(@RequestBody TermImportConfirmDto dto) {
        if (dto.getTerms() == null || dto.getTerms().isEmpty()) {
            return R.error(400, "导入列表不能为空");
        }
        int count = termImportService.batchImportByPath(dto.getTerms(), dto.getProjectName());

        Map<String, Object> result = new HashMap<>();
        result.put("imported", count);
        return R.ok(result);
    }
}
