package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.PermApply;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.entity.SysUserRole;
import com.unilex.backend.mapper.PermApplyMapper;
import com.unilex.backend.mapper.SysRoleMapper;
import com.unilex.backend.service.PermApplyService;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysUserRoleService;
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
    private final SysUserRoleService userRoleService;

    @Override
    @Transactional
    public PermApply submitApply(Long userId, Long roleId, String reason) {
        /* 角色必须存在 */
        if (roleId == null || roleMapper.selectById(roleId) == null) {
            throw new IllegalArgumentException("申请的角色不存在");
        }

        /* 已拥有该角色则无需申请 */
        long owned = userRoleService.lambdaQuery()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId)
                .count();
        if (owned > 0) {
            throw new IllegalArgumentException("您已拥有该角色，无需重复申请");
        }

        /* 同一角色已有待审申请，禁止重复提交 */
        long pending = lambdaQuery()
                .eq(PermApply::getApplicantId, userId)
                .eq(PermApply::getRoleId, roleId)
                .eq(PermApply::getStatus, 1)
                .count();
        if (pending > 0) {
            throw new IllegalArgumentException("该角色的申请正在审批中，请勿重复提交");
        }

        PermApply apply = new PermApply();
        apply.setApplicantId(userId);
        apply.setRoleId(roleId);
        apply.setReason(reason);
        apply.setStatus(1);
        save(apply);
        return apply;
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
            SysRole role = roleMapper.selectById(po.getRoleId());
            vo.setRoleDesc(role == null ? "-" : role.getDesc());

            return vo;
        });
    }

    @Override
    @Transactional
    public void audit(Long applyId, Long approverId, Integer status, String approveMsg) {
        /* 审批状态只允许 2-通过 / 3-驳回 */
        if (status == null || (status != 2 && status != 3)) {
            throw new IllegalArgumentException("非法的审批状态");
        }

        PermApply one = getById(applyId);
        if (one == null || !one.getStatus().equals(1)) {
            throw new IllegalArgumentException("申请不存在或已审批");
        }

        /* 禁止审批自己提交的申请 */
        if (one.getApplicantId().equals(approverId)) {
            throw new IllegalArgumentException("不能审批自己提交的申请");
        }

        /* 通过前确认角色仍然存在 */
        if (status == 2 && roleMapper.selectById(one.getRoleId()) == null) {
            throw new IllegalArgumentException("申请的角色已被删除，无法通过");
        }

        one.setStatus(status);
        one.setApproverId(approverId);
        one.setApproveMsg(approveMsg);
        one.setUpdatedAt(LocalDateTime.now());
        updateById(one);

        // 审批通过 → 把角色授予申请人（幂等：已拥有则跳过，不影响其已有角色）
        if (status == 2) {
            long granted = userRoleService.lambdaQuery()
                    .eq(SysUserRole::getUserId, one.getApplicantId())
                    .eq(SysUserRole::getRoleId, one.getRoleId())
                    .count();
            if (granted == 0) {
                userRoleService.save(new SysUserRole(one.getApplicantId(), one.getRoleId()));
            }
            // 使申请人权限缓存失效，新角色即时生效
            SysUser applicant = userService.getById(one.getApplicantId());
            if (applicant != null) {
                permService.evictUserPermCache(applicant.getUsername());
            }
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
            SysRole role = roleMapper.selectById(po.getRoleId());
            vo.setRoleDesc(role == null ? "-" : role.getDesc());

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

        SysRole role = roleMapper.selectById(po.getRoleId());
        vo.setRoleDesc(role == null ? "-" : role.getDesc());

        return vo;
    }
}