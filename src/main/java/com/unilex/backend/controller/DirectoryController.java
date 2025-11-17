package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.vo.DirTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}