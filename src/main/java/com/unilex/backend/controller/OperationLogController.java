package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.entity.OperationLog;
import com.unilex.backend.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping
    public R<Page<OperationLog>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null, OperationLog::getUsername, username)
               .eq(module != null, OperationLog::getModule, module)
               .eq(operation != null, OperationLog::getOperation, operation);

        // keyword 模糊搜索：描述、URL、操作人
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(OperationLog::getDescription, keyword)
                    .or().like(OperationLog::getUsername, keyword)
                    .or().like(OperationLog::getUrl, keyword));
        }

        wrapper.orderByDesc(OperationLog::getCreatedAt);

        Page<OperationLog> page = operationLogService.page(new Page<>(current, size), wrapper);
        return R.ok(page);
    }
}
