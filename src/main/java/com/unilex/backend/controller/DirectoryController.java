package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.vo.DirAddVo;
import com.unilex.backend.vo.DirTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 新增目录（可挂在任意级，自动计算排序）
     */
    @PostMapping("")
    public R<Long> addDir(@RequestBody DirAddVo vo){
        Long newId = directoryService.addDir(vo);
        return R.ok(newId);
    }
}