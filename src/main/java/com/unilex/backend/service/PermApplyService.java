package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.PermApply;
import com.unilex.backend.vo.PermApplyVo;

public interface PermApplyService extends IService<PermApply> {
    /** 提交申请 **/
    void submitApply(Long userId, Long permId, String reason);

    Page<PermApplyVo> pageApply(long current, long size, Integer status);
    void audit(Long applyId, Long approverId, Integer status, String approveMsg);
}