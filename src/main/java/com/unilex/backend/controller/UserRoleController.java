package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.entity.SysUserRole;
import com.unilex.backend.security.ReqPerm;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysRoleService;
import com.unilex.backend.service.SysUserRoleService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.vo.UserPageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-role")
@RequiredArgsConstructor
public class UserRoleController {

    private final SysUserService userService;
    private final SysUserRoleService userRoleService;
    private final SysRoleService roleService;
    private final SysPermService permService;

    /* ------ 1. 用户分页（含已有角色 id 列表） ------ */
    @GetMapping("/users")
    @ReqPerm("user:role")
    public R<Page<UserPageVo>> users(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<SysUser> poPage = userService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<SysUser>()
                        .like(keyword != null, SysUser::getUsername, keyword)
                        .orderByDesc(SysUser::getCreatedAt)
        );

        Page<UserPageVo> voPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        List<UserPageVo> records = poPage.getRecords().stream().map(u -> {
            List<Long> roleIds = roleService.listByUserId(u.getId())
                    .stream().map(SysRole::getId).collect(Collectors.toList());
            return UserPageVo.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .roleIdList(roleIds)
                    .build();
        }).collect(Collectors.toList());
        voPage.setRecords(records);
        return R.ok(voPage);
    }

    /* ------ 2. 全部角色（用于 Transfer） ------ */
    @GetMapping("/roles")
    @ReqPerm("user:role")
    public R<List<SysRole>> roles() {
        return R.ok(roleService.listAll());
    }

    /* ------ 3. 保存用户角色 ------ */
    @PutMapping("/{userId}")
    @ReqPerm("user:role")
    public R<Void> saveRoles(@PathVariable Long userId, @RequestBody List<Long> roleIdList) {
        // 先删后插
        userRoleService.lambdaUpdate().eq(SysUserRole::getUserId, userId).remove();
        if (roleIdList != null && !roleIdList.isEmpty()) {
            List<SysUserRole> list = roleIdList.stream()
                    .map(rid -> new SysUserRole(userId, rid))
                    .collect(Collectors.toList());
            userRoleService.saveBatch(list);
        }
        // 角色变更后使该用户的权限缓存失效
        SysUser target = userService.getById(userId);
        if (target != null) {
            permService.evictUserPermCache(target.getUsername());
        }
        return R.ok(null);
    }
}