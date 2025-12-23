package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.PermApply;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.mapper.PermApplyMapper;
import com.unilex.backend.mapper.SysRoleMapper;
import com.unilex.backend.service.PermApplyService;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.vo.PermApplyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermApplyServiceImpl extends ServiceImpl<PermApplyMapper, PermApply>
        implements PermApplyService {

    private final SysUserService userService;
    private final SysPermService permService;
    private final SysRoleMapper roleMapper;

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
    public Page<PermApplyVo> pageApply(long current, long size, Integer status) {
        Page<PermApply> p = lambdaQuery()
                .eq(status != null, PermApply::getStatus, status)
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
            SysPerm perm = permService.getById(po.getPermId());
            vo.setPermDesc(perm == null ? "-" : perm.getPermName());

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
        updateById(one);
    }
}