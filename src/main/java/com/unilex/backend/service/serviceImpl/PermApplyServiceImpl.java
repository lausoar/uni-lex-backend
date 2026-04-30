package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.PermApply;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.mapper.PermApplyMapper;
import com.unilex.backend.mapper.SysRoleMapper;
import com.unilex.backend.mapper.SysUserRoleMapper;
import com.unilex.backend.service.PermApplyService;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.vo.PermApplyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限申请服务实现
 * <p>处理用户权限申请提交、审批流转及申请记录分页查询</p>
 */
@Service
@RequiredArgsConstructor
public class PermApplyServiceImpl extends ServiceImpl<PermApplyMapper, PermApply>
        implements PermApplyService {

    private final SysUserService userService;
    private final SysPermService permService;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    @Transactional
    public void submitApply(Long userId, Long permId, String reason) {
        PermApply apply = new PermApply();
        apply.setApplicantId(userId);
        apply.setPermId(permId);
        apply.setReason(reason);
        apply.setStatus(1);
        save(apply);
    }

    @Override
    public Page<PermApplyVo> pageApply(long current, long size, List<Integer> statusList) {
        Page<PermApply> p = lambdaQuery()
                .in(statusList != null && !statusList.isEmpty(),
                        PermApply::getStatus, statusList)
                .orderByDesc(PermApply::getCreatedAt)
                .page(new Page<>(current, size));

        return (Page<PermApplyVo>) p.convert(po -> {
            PermApplyVo vo = new PermApplyVo();
            BeanUtils.copyProperties(po, vo);

            // 申请人
            SysUser user = userService.getById(po.getApplicantId());
            vo.setApplicantName(user == null ? "-" : user.getUsername());

            // 审批人
            if (po.getApproverId() != null) {
                SysUser approver = userService.getById(po.getApproverId());
                vo.setApproverName(approver == null ? "-" : approver.getUsername());
            }

            // 权限名称
            SysRole role = roleMapper.selectById(po.getPermId());
            vo.setPermDesc(role == null ? "-" : role.getDesc());

            return vo;
        });
    }

    @Override
    @Transactional
    public void audit(Long applyId, Long approverId, Integer status, String approveMsg) {
        PermApply one = getById(applyId);
        if (one == null || !one.getStatus().equals(1)) {
            throw new RuntimeException("申请不存在或已审批");
        }
        one.setStatus(status);
        one.setApproverId(approverId);
        one.setApproveMsg(approveMsg);
        one.setUpdatedAt(LocalDateTime.now());
        updateById(one);

        // 审批通过 → 把权限授予申请人
        if (status == 2) {
            // 先删再插，避免重复
            userRoleMapper.deleteByUserIdAndRoleId(one.getApplicantId());
            // 插入授权记录F
            userRoleMapper.insertRole(one.getApplicantId(), one.getPermId());
        }
    }

    @Override
    public Page<PermApplyVo> pageApplyByUser(Long userId, long current, long size, List<Integer> statusList) {
        Page<PermApply> p = lambdaQuery()
                .eq(PermApply::getApplicantId, userId)
                .in(statusList != null && !statusList.isEmpty(),
                        PermApply::getStatus, statusList)
                .orderByDesc(PermApply::getCreatedAt)
                .page(new Page<>(current, size));

        return (Page<PermApplyVo>) p.convert(po -> {
            PermApplyVo vo = new PermApplyVo();
            BeanUtils.copyProperties(po, vo);
            // 申请人就是当前登录人，controller 已统一赋值，这里跳过
            // vo.setApplicantName(...);

            // 权限名称
            SysRole role = roleMapper.selectById(po.getPermId());
            vo.setPermDesc(role == null ? "-" : role.getDesc());

            // 审批人
            if (po.getApproverId() != null) {
                SysUser approver = userService.getById(po.getApproverId());
                vo.setApproverName(approver == null ? "-" : approver.getUsername());
            }
            return vo;
        });
    }

    @Override
    public PermApplyVo singleVo(Long id) {
        PermApply po = getById(id);
        PermApplyVo vo = new PermApplyVo();
        BeanUtils.copyProperties(po, vo);

        SysUser user = userService.getById(po.getApplicantId());
        vo.setApplicantName(user == null ? "-" : user.getUsername());

        SysRole role = roleMapper.selectById(po.getPermId());
        vo.setPermDesc(role == null ? "-" : role.getDesc());

        return vo;
    }
}