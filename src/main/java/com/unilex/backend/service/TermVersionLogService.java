package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.TermVersionLog;

import java.util.List;

public interface TermVersionLogService extends IService<TermVersionLog> {

    /** 记录版本变更 */
    void logVersionChange(Long termId, Long dirId, String shortKey,
                          String oldVersion, String newVersion, String changeType);

    /** 查询某个术语的版本历史 */
    List<TermVersionLog> listByTermId(Long termId);

    /** 按 shortKey 查询版本历史 */
    List<TermVersionLog> listByShortKey(String shortKey);
}
