package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysRoleService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.vo.UserPageVo;
import com.unilex.backend.vo.UserSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService userService;
    private final SysPermService permService;   // 已有
    private final PasswordEncoder encoder;
    private final SysRoleService roleService;   // 已有

    /* ------ 分页 + 搜索 ------ */
    @GetMapping
    public R<Page<UserPageVo>> page(
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
            /* 1. 真实角色列表（可能多个） */
            List<SysRole> roles = roleService.listByUserId(u.getId());
            List<String> roleNames = roles.stream().map(SysRole::getName).collect(Collectors.toList());
            List<String> roleDescs = roles.stream().map(SysRole::getDesc).collect(Collectors.toList());

            /* 3. 权限码照旧 */
            List<String> perms = permService.listUserPerms(u.getUsername());

            List<Long>   roleIds   = roleService.listByUserId(u.getId())
                    .stream().map(SysRole::getId).collect(Collectors.toList());
            List<String> permCodes = permService.listUserPerms(u.getUsername());

            return UserPageVo.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .status(u.getStatus())
                    .role(roleNames.isEmpty() ? "暂无角色" : roleNames.get(0))
                    .roleDesc(roleDescs.isEmpty() ? "暂无角色" : roleDescs.get(0))
                    .roleIdList(roles.stream().map(SysRole::getId).collect(Collectors.toList()))
                    .permissions(perms)
                    .permCodeList(perms)
                    .createdAt(u.getCreatedAt())
                    .updatedAt(u.getUpdatedAt())
                    .roleIdList(roleIds)
                    .permCodeList(permCodes)
                    .build();
        }).collect(Collectors.toList());

        voPage.setRecords(records);
        return R.ok(voPage);
    }

    /* ------ 新增 ------ */
    @PostMapping
    public R<Void> add(@RequestBody UserSaveVo vo) {
        if (userService.exist(vo.getUsername())) {
            return R.conflict("账号已存在");
        }
        SysUser u = new SysUser();
        u.setUsername(vo.getUsername());
        u.setPassword(encoder.encode(vo.getPassword()));
        u.setStatus(vo.getStatus());
        userService.saveUserWithRoleAndPerm(u, vo.getRoleIdList(), vo.getPermCodeList());
        return R.ok(null);
    }

    /* ------ 编辑 ------ */
    @PutMapping("/{id}")
    public R<Void> upd(@PathVariable Long id, @RequestBody UserSaveVo vo) {
        SysUser u = userService.getById(id);
        if (u == null) return R.error(404, "用户不存在");
        u.setUsername(vo.getUsername());
        if (vo.getPassword() != null)
            u.setPassword(encoder.encode(vo.getPassword()));
        u.setStatus(vo.getStatus());
        userService.saveUserWithRoleAndPerm(u, vo.getRoleIdList(), vo.getPermCodeList());
        return R.ok(null);
    }

    /* ------ 删除 ------ */
    @DeleteMapping("/{id}")
    public R<Void> del(@PathVariable Long id) {
        userService.removeById(id);
        return R.ok(null);
    }

    /* ------ 重置密码 ------ */
    @PatchMapping("/{id}/pwd")
    public R<Void> resetPwd(@PathVariable Long id,
                            @RequestParam(defaultValue = "123456789") String password) {
        SysUser u = userService.getById(id);
        if (u == null) return R.error(404, "用户不存在");
        u.setPassword(encoder.encode(password));
        userService.updateById(u);
        return R.ok(null);
    }

    /* ------ 下拉框数据 ------ */
    @GetMapping("/roles")
    public R<List<SysRole>> roles() {
        return R.ok(roleService.listAll());
    }

    @GetMapping("/perms")
    public R<List<SysPerm>> perms() {
        return R.ok(permService.listAll());
    }
}