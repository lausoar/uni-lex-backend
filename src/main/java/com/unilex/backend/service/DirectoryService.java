package com.unilex.backend.service;

import com.unilex.backend.vo.DirAddVo;
import com.unilex.backend.vo.DirTreeVo;

import java.util.List;

public interface DirectoryService {
    /**
     * 返回整棵树，已排好序，前端直接渲染
     */
    List<DirTreeVo> wholeTree(String lang);

    Long addDir(DirAddVo vo);
}
