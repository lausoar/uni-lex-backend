package com.unilex.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.dto.AuditDto;
import com.unilex.backend.dto.PermApplyDto;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.service.PermApplyService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.vo.PermApplyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/perm-apply")
@RequiredArgsConstructor
public class PermApplyController {

    private final PermApplyService permApplyService;
    private final SysUserService userService;

    /** 前端：提交权限申请 */
    @PostMapping
    public R<Void> submit(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestBody PermApplyDto dto) {

        // 1. 先用登录名把用户整对象查出来
        SysUser user = userService.lambdaQuery()
                .eq(SysUser::getUsername, userDetails.getUsername())
                .one();
        if (user == null) {
            return R.error(400, "用户不存在");
        }

        // 2. 拿真正的主键去申请
        permApplyService.submitApply(user.getId(), dto.getPermId(), dto.getReason());
        return R.ok(null);
    }

    /** 后台：分页查询申请 */
    @GetMapping("/page")
    public R<Page<PermApplyVo>> page(@RequestParam(defaultValue = "1") long current,
                                     @RequestParam(defaultValue = "10") long size,
                                     @RequestParam(required = false) Integer status) {
        return R.ok(permApplyService.pageApply(current, size, status));
    }

    /** 后台：审批 */
    @PatchMapping("/{id}/audit")
    public R<Void> audit(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails user,
                         @RequestBody AuditDto dto) {
        Long approverId = Long.valueOf(user.getUsername()); // 同之前，确保这里已是用户主键
        permApplyService.audit(id, approverId, dto.getStatus(), dto.getApproveMsg());
        return R.ok(null);
    }
}