package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.vo.TermFlagsUpdateVo;
import com.unilex.backend.vo.TermRowVo;

import java.util.List;
import java.util.Map;

/**
 * 术语条目服务接口
 * <p>提供术语的增删改查、多维度搜索、批量排序及状态标记功能</p>
 */
public interface TermService extends IService<TermEntry> {

    /**
     * 查询某个目录下的术语列表
     * @param dirId 目录ID
     * @return 术语视图列表
     */
    List<TermRowVo> listTerm(Long dirId);

    /**
     * 更新术语信息
     * @param id 术语ID
     * @param vo 术语更新参数
     */
    void updateTerm(Long id, TermRowVo vo);

    /**
     * 新增术语
     * @param vo 术语新增参数
     * @return 新增后的术语实体
     */
    TermEntry addTerm(TermRowVo vo);

    /**
     * 更新术语状态标记（确认/预定义）
     * @param id 术语ID
     * @param vo 状态更新参数
     */
    void updateFlags(Long id, TermFlagsUpdateVo vo);

    /**
     * 多维度搜索术语
     * @param products 产品筛选（SmartOM/EMS/OnePoint）
     * @param dataType 数据类型（目录ID）
     * @param keyword 关键词（匹配shortKey/多语言名称）
     * @param projects 所属项目筛选
     * @param confirm 确认状态筛选（pending/confirmed/all）
     * @return 术语视图列表
     */
    List<TermRowVo> search(List<String> products, String dataType, String keyword, List<String> projects, String confirm);

    /**
     * 批量排序
     * @param idOrderMap key: termId, value: newSortOrder
     * @return 返回更新后的 id -> sortOrder 映射
     */
    Map<Long, Integer> batchSort(Map<Long, Integer> idOrderMap);
}