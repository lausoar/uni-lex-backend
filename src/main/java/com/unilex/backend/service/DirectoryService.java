package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.vo.DirAddVo;
import com.unilex.backend.vo.DirSaveVo;
import com.unilex.backend.vo.DirTreeVo;

import java.util.List;

public interface DirectoryService extends IService<CatDirectory> {
    /**
     * 返回整棵树，已排好序，前端直接渲染
     */
    List<DirTreeVo> wholeTree(String lang);

    Long addDir(DirAddVo vo);

    void saveDir(DirSaveVo vo);
    void delDir(Long id);
}
