package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.PermApply;
import com.unilex.backend.vo.PermApplyVo;

import java.util.List;

/**
 * 权限申请服务接口
 * <p>处理用户权限申请、审批及分页查询等业务</p>
 */
public interface PermApplyService extends IService<PermApply> {

    /**
     * 提交权限申请（实际申请的是角色）
     * @param userId 申请人用户ID
     * @param roleId 申请的角色ID
     * @param reason 申请理由
     * @return 已保存的申请记录
     * @throws IllegalArgumentException 角色不存在、已拥有该角色或存在待审的重复申请时
     */
    PermApply submitApply(Long userId, Long roleId, String reason);

    /**
     * 分页查询所有申请记录
     * @param current 当前页码
     * @param size 每页大小
     * @param status 状态筛选列表（可选）
     * @return 分页申请记录
     */
    Page<PermApplyVo> pageApply(long current, long size, List<Integer> status);

    /**
     * 审批申请
     * @param applyId 申请记录ID
     * @param approverId 审批人用户ID
     * @param status 审批状态（2-通过，3-拒绝）
     * @param approveMsg 审批意见
     */
    void audit(Long applyId, Long approverId, Integer status, String approveMsg);

    /**
     * 查询单条申请详情
     * @param id 申请记录ID
     * @return 申请详情VO
     */
    PermApplyVo singleVo(Long id);

    /**
     * 按用户分页查询申请记录
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @param statusList 状态筛选列表（可选）
     * @return 分页申请记录
     */
    Page<PermApplyVo> pageApplyByUser(Long userId, long current, long size, List<Integer> statusList);

}