package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unilex.backend.common.R;
import com.unilex.backend.dto.TermSearchRequest;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.security.OpLog;
import com.unilex.backend.security.ReqPerm;
import com.unilex.backend.service.TermService;
import com.unilex.backend.vo.TermBatchSortVo;
import com.unilex.backend.vo.TermFlagsUpdateVo;
import com.unilex.backend.vo.TermRowVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/term")
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @GetMapping("/list")
    public R<List<TermRowVo>> list(@RequestParam(required = false) Long dirId) {
        return R.ok(termService.listTerm(dirId));
    }

    @GetMapping("/byLeaf")
    public List<TermRowVo> byLeaf(@RequestParam Long dirId) {
        return termService.listTerm(dirId);   // 直接用现成的实现
    }

    @PutMapping("/{id}")
    @ReqPerm("term:update")
    @OpLog(module = "term", operation = "UPDATE", description = "修改术语")
    public R<Void> update(@PathVariable Long id,
                          @RequestBody TermRowVo vo) {
        termService.updateTerm(id, vo);
        return R.ok(null);
    }

    @DeleteMapping("/{id}")
    @ReqPerm("term:delete")
    @OpLog(module = "term", operation = "DELETE", description = "删除术语")
    public R<Void> delete(@PathVariable Long id) {
        termService.removeById(id);
        return R.ok(null);
    }

    /**
     * 新增术语（挂在指定目录下）
     */
    @PostMapping
    @ReqPerm("term:add")
    @OpLog(module = "term", operation = "CREATE", description = "新增术语")
    public R<TermRowVo> add(@RequestBody TermRowVo vo) {
        TermEntry newEntry = termService.addTerm(vo);
        // 把刚插入的实体回显给前端（含主键 id）
        return R.ok(TermRowVo.builder()
                .id(newEntry.getId())
                .shortKey(newEntry.getShortKey())
                .definition(newEntry.getDefinition())
                .zhCn(newEntry.getZhCn())
                .enUs(newEntry.getEnUs())
                .jaJp(newEntry.getJaJp())
                .projectName(newEntry.getProjectName())
                .productSmartom(newEntry.getProductSmartom() == 1)
                .productEms(newEntry.getProductEms() == 1)
                .productOnepoint(newEntry.getProductOnepoint() == 1)
                .predefined(newEntry.getIsPredefined() == 1)
                .confirmed(newEntry.getConfirmed() == 1)
                .sortOrder(newEntry.getSortOrder())
                .build());
    }

    /**
     * 仅更新 confirmed / is_predefined 两个字段
     */
    @PatchMapping("/{id}/flags")
    @ReqPerm("term:confirm")
    @OpLog(module = "term", operation = "UPDATE", description = "更新术语状态标记")
    public R<Void> updateFlags(@PathVariable Long id,
                               @RequestBody @Validated TermFlagsUpdateVo vo) {
        termService.updateFlags(id, vo);
        return R.ok(null);
    }


    @PostMapping("/search")
    public R<List<TermRowVo>> search(@RequestBody TermSearchRequest req) {
        System.out.println("Controller 收到 products: " + req.getProducts());

        return R.ok(termService.search(
                req.getProducts(),
                req.getDataType(),
                req.getKeyword(),
                req.getProjects(),
                req.getConfirm()
        ));
    }

    @GetMapping("/projects")
    public R<List<String>> allProjects() {
        List<String> list = termService.lambdaQuery()
                .isNotNull(TermEntry::getProjectName)
                .ne(TermEntry::getProjectName, "")
                .orderByAsc(TermEntry::getProjectName)
                .list()
                .stream()
                .map(TermEntry::getProjectName)
                .distinct()
                .collect(Collectors.toList());
        return R.ok(list);
    }

    /**
     * 批量更新术语排序（拖拽排序后一次性保存）
     */
    @PostMapping("/batch-sort")
    @ReqPerm("term:update")
    @OpLog(module = "term", operation = "UPDATE", description = "批量排序术语")
    public R<Map<Long,Integer>> batchSort(@RequestBody @Validated TermBatchSortVo vo){
        return R.ok(termService.batchSort(vo.getIdOrderMap()));
    }

    /**
     * 批量确认
     * @param ids 术语主键列表
     */
    @PostMapping("/batch-confirm")
    @OpLog(module = "term", operation = "UPDATE", description = "批量确认术语")
    public R<Void> batchConfirm(@RequestBody List<Long> ids) {
        if (ids.isEmpty()) return R.ok(null);
        termService.lambdaUpdate()
                .set(TermEntry::getConfirmed, 1)
                .set(TermEntry::getIsPredefined, 1)
                .in(TermEntry::getId, ids)
                .update();
        return R.ok(null);
    }
}
