package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.entity.TermVersionLog;
import com.unilex.backend.service.TermVersionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 术语版本历史控制器
 */
@RestController
@RequestMapping("/api/term/version-log")
@RequiredArgsConstructor
public class TermVersionLogController {

    private final TermVersionLogService versionLogService;

    /**
     * 按术语 ID 查询版本历史
     */
    @GetMapping("/by-term/{termId}")
    public R<List<TermVersionLog>> byTerm(@PathVariable Long termId) {
        return R.ok(versionLogService.listByTermId(termId));
    }

    /**
     * 按 shortKey 查询版本历史（同一术语在不同目录下的所有版本记录）
     */
    @GetMapping("/by-key")
    public R<List<TermVersionLog>> byKey(@RequestParam String shortKey) {
        return R.ok(versionLogService.listByShortKey(shortKey));
    }
}
