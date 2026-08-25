package com.unilex.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.dto.AuditDto;
import com.unilex.backend.dto.PermApplyDto;
import com.unilex.backend.entity.PermApply;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.security.OpLog;
import com.unilex.backend.security.ReqPerm;
import com.unilex.backend.service.PermApplyService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.vo.PermApplyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/perm-apply")
@RequiredArgsConstructor
public class PermApplyController {

    private final PermApplyService permApplyService;
    private final SysUserService userService;

    /** 前端：提交权限申请（实际申请的是角色，校验规则见 service 层） */
    @PostMapping
    @OpLog(module = "perm", operation = "CREATE", description = "提交权限申请")
    public R<PermApplyVo> submit(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestBody PermApplyDto dto) {
        SysUser user = userService.lambdaQuery()
                .eq(SysUser::getUsername, userDetails.getUsername())
                .one();
        if (user == null) return R.error(400, "用户不存在");

        PermApply saved = permApplyService.submitApply(user.getId(), dto.getRoleId(), dto.getReason());
        return R.ok(permApplyService.singleVo(saved.getId()));
    }

    /** 后台：分页查询申请 */
    @GetMapping("/page")
    @ReqPerm("perm:audit")
    public R<Page<PermApplyVo>> page(
            @RequestParam(defaultValue = "1")  long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false)    String status) {

        List<Integer> statusList = null;
        if (status != null && !status.trim().isEmpty()) {
            statusList = Arrays.stream(status.split(","))
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        return R.ok(permApplyService.pageApply(current, size, statusList));
    }

    /** 后台：审批（service 内含状态校验与禁止自审） */
    @PatchMapping("/{id}/audit")
    @ReqPerm("perm:audit")
    @OpLog(module = "perm", operation = "AUDIT", description = "审批权限申请")
    public R<Void> audit(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails user,
                         @RequestBody AuditDto dto) {
        SysUser approver = userService.lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .one();
        if (approver == null) throw new RuntimeException("审批人不存在");
        Long approverId = approver.getId();
        permApplyService.audit(id, approverId, dto.getStatus(), dto.getApproveMsg());
        return R.ok(null);
    }

    /** 当前登录人查看自己提交的申请 */
    /* 当前登录人查看自己提交的申请 */
    @GetMapping("/my")
    public R<Page<PermApplyVo>> my(
            @RequestParam(defaultValue = "1")  long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false)    String status,   // 1 待审批 2,3 已审批
            @AuthenticationPrincipal UserDetails user) {

        SysUser me = userService.lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .one();
        if (me == null) return R.error(400, "用户不存在");

        List<Integer> statusList = null;
        if (status != null && !status.trim().isEmpty()) {
            statusList = Arrays.stream(status.split(","))
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }

        Page<PermApplyVo> page = permApplyService.pageApplyByUser(me.getId(), current, size, statusList);

        /* 关键：把当前登录人姓名写进去（前端进度面板直接显示） */
        page.getRecords().forEach(vo -> vo.setApplicantName(me.getUsername()));

        return R.ok(page);
    }


}