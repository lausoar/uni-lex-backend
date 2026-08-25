package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.security.OpLog;
import com.unilex.backend.security.ReqPerm;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.vo.DirAddVo;
import com.unilex.backend.vo.DirSaveVo;
import com.unilex.backend.vo.DirTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dir")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    /**
     * 获取术语目录树
     * @param lang zh 或 en
     */
    @GetMapping("/tree")
    public R<List<DirTreeVo>> tree(@RequestParam(defaultValue = "zh") String lang) {
        return R.ok(directoryService.wholeTree(lang));
    }

    /**
     * 新增目录（支持指定排序位置）
     */
    @PostMapping("")
    @ReqPerm("dir:add")
    @OpLog(module = "directory", operation = "CREATE", description = "新增目录")
    public R<Long> addDir(@RequestBody Map<String, Object> params) {
        DirAddVo vo = new DirAddVo();
        vo.setLevel((Integer) params.get("level"));
        vo.setLevel1Id(params.get("level1Id") != null ? Long.valueOf(params.get("level1Id").toString()) : null);
        vo.setLevel2Id(params.get("level2Id") != null ? Long.valueOf(params.get("level2Id").toString()) : null);
        vo.setName((String) params.get("name"));

        Integer targetSortOrder = params.get("targetSortOrder") != null ?
                Integer.valueOf(params.get("targetSortOrder").toString()) : null;

        Long newId;
        if (targetSortOrder != null) {
            newId = directoryService.addDirWithSort(vo, targetSortOrder);
        } else {
            newId = directoryService.addDir(vo);
        }
        return R.ok(newId);
    }

    /**
     * 批量更新目录排序
     */
    @PostMapping("/batch-update-sort")
    @ReqPerm("dir:add")
    @OpLog(module = "directory", operation = "UPDATE", description = "批量排序目录")
    public R<Void> batchUpdateSort(@RequestBody List<DirSaveVo> sortList) {
        directoryService.batchUpdateSort(sortList);
        return R.ok(null);
    }
}
