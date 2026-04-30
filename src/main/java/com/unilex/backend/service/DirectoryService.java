package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.vo.DirAddVo;
import com.unilex.backend.vo.DirSaveVo;
import com.unilex.backend.vo.DirTreeVo;

import java.util.List;

/**
 * 目录分类服务接口
 * <p>管理三级目录树结构，支持增删改查及排序调整</p>
 */
public interface DirectoryService extends IService<CatDirectory> {

    /**
     * 返回整棵目录树，已按 sort_order 排好序，前端直接渲染
     * @param lang 语言标识（如 zh/en），决定返回的目录名称
     * @return 根节点列表
     */
    List<DirTreeVo> wholeTree(String lang);

    /**
     * 新增目录（自动计算排序号）
     * @param vo 目录新增参数
     * @return 新增目录的ID
     */
    Long addDir(DirAddVo vo);

    /**
     * 保存（新增或更新）目录信息
     * @param vo 目录保存参数
     */
    void saveDir(DirSaveVo vo);

    /**
     * 删除指定目录
     * @param id 目录ID
     */
    void delDir(Long id);

    /**
     * 新增目录并指定排序位置
     * @param vo 目录新增参数
     * @param targetSortOrder 目标排序号（为空则默认排在最后）
     * @return 新增目录的ID
     */
    Long addDirWithSort(DirAddVo vo, Integer targetSortOrder);

    /**
     * 批量更新目录排序
     * @param sortList 排序参数列表
     */
    void batchUpdateSort(List<DirSaveVo> sortList);
}