package com.unilex.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.dto.AuditDto;
import com.unilex.backend.dto.PermApplyDto;
import com.unilex.backend.entity.PermApply;
import com.unilex.backend.entity.SysUser;
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

    /** 前端：提交权限申请 */
//    @PostMapping
//    public R<Void> submit(@AuthenticationPrincipal UserDetails userDetails,
//                          @RequestBody PermApplyDto dto) {
//
//        // 1. 先用登录名把用户整对象查出来
//        SysUser user = userService.lambdaQuery()
//                .eq(SysUser::getUsername, userDetails.getUsername())
//                .one();
//        if (user == null) {
//            return R.error(400, "用户不存在");
//        }
//
//        // 2. 拿真正的主键去申请
//        permApplyService.submitApply(user.getId(), dto.getPermId(), dto.getReason());
//        return R.ok(null);
//    }

    @PostMapping
    public R<PermApplyVo> submit(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestBody PermApplyDto dto) {
        SysUser user = userService.lambdaQuery()
                .eq(SysUser::getUsername, userDetails.getUsername())
                .one();
        if (user == null) return R.error(400, "用户不存在");

        // 1. 保存
        PermApply apply = new PermApply();
        apply.setApplicantId(user.getId());
        apply.setPermId(dto.getPermId());
        apply.setReason(dto.getReason());
        apply.setStatus(1);
        permApplyService.save(apply);

        // 2. 组装 Vo（和列表接口一样）
        PermApplyVo vo = permApplyService.singleVo(apply.getId());
        return R.ok(vo);
    }

    /** 后台：分页查询申请 */
    @GetMapping("/page")
    public R<Page<PermApplyVo>> page(
            @RequestParam(defaultValue = "1")  long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false)    String status) {

        List<Integer> statusList = null;
        if (status != null && !status.isBlank()) {
            statusList = Arrays.stream(status.split(","))
                    .map(String::trim)          // 去掉前后空格
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        return R.ok(permApplyService.pageApply(current, size, statusList));
    }

    /** 后台：审批 */
    @PatchMapping("/{id}/audit")
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
        if (status != null && !status.isBlank()) {
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