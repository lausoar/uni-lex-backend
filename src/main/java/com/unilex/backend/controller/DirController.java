package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.vo.DirPageVo;
import com.unilex.backend.vo.DirSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dir/admin")
@RequiredArgsConstructor
public class DirController {

    private final DirectoryService dirService;

    /* 1. 整棵树（平表→树，前端直接渲染） */
    @GetMapping("/tree")
    public R<List<CatDirectory>> tree() {
        List<CatDirectory> flat = dirService.lambdaQuery()
                .orderByAsc(CatDirectory::getParentId, CatDirectory::getSortOrder)
                .list();
        return R.ok(flat);
    }

    /* 2. 分页列表（平表，用于表格底部） */
    @GetMapping
    public R<Page<DirPageVo>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<CatDirectory> poPage = dirService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<CatDirectory>()
                        .and(keyword != null,
                                q -> q.like(CatDirectory::getDirNameZh, keyword)
                                        .or()
                                        .like(CatDirectory::getDirKey, keyword))
                        .orderByAsc(CatDirectory::getParentId, CatDirectory::getSortOrder)
        );

        Page<DirPageVo> voPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        List<DirPageVo> records = poPage.getRecords().stream().map(d ->
                DirPageVo.builder()
                        .id(d.getId())
                        .parentId(d.getParentId())
                        .dirType(d.getDirType())
                        .dirKey(d.getDirKey())
                        .dirNameZh(d.getDirNameZh())
                        .dirNameEn(d.getDirNameEn())
                        .sortOrder(d.getSortOrder())
                        .isSystem(d.getIsSystem() == 1)
                        .createdAt(d.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());
        voPage.setRecords(records);
        return R.ok(voPage);
    }

    /* 3. 新增 or 编辑 */
    @PostMapping
    public R<Void> add(@RequestBody DirSaveVo vo) {
        dirService.saveDir(vo);
        return R.ok(null);
    }

    @PutMapping("/{id}")
    public R<Void> upd(@PathVariable Long id, @RequestBody DirSaveVo vo) {
        vo.setId(id);
        dirService.saveDir(vo);
        return R.ok(null);
    }

    /* 4. 删除 */
    @DeleteMapping("/{id}")
    public R<Void> del(@PathVariable Long id) {
        dirService.delDir(id);
        return R.ok(null);
    }
}