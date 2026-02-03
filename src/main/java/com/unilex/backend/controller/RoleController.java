package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.entity.*;
import com.unilex.backend.mapper.SysRoleMapper;
import com.unilex.backend.mapper.SysUserMapper;
import com.unilex.backend.mapper.SysUserRoleMapper;
import com.unilex.backend.service.*;
import com.unilex.backend.vo.RolePageVo;
import com.unilex.backend.vo.RoleSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleService roleService;
    private final SysRoleMapper roleMapper;
    private final SysPermService permService;
    private final SysRolePermService sysRolePermService;
    private final SysUserService userService;
    private final SysUserRoleMapper userRoleMapper;

    /* ------ 分页+搜索 ------ */
    @GetMapping
    public R<Page<RolePageVo>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<SysRole> poPage = roleService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<SysRole>()
                        .like(keyword != null, SysRole::getName, keyword)
                        .or()
                        .like(keyword != null, SysRole::getDesc, keyword)
                        .orderByAsc(SysRole::getId)
        );

        Page<RolePageVo> voPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        List<RolePageVo> records = poPage.getRecords().stream().map(r ->
                RolePageVo.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .desc(r.getDesc())
                        .permList(roleMapper.listPermCodesByRoleId(r.getId()))
                        .createdAt(LocalDateTime.now()) // 表无该字段，mock
                        .build()
        ).collect(Collectors.toList());
        voPage.setRecords(records);
        return R.ok(voPage);
    }

    /** 返回全部角色（权限申请时下拉框用） */
    @GetMapping("/all")
    public R<List<SysRole>> all() {
        List<SysRole> list = roleService.list();
        return R.ok(list);
    }

    /* ------ 新增 ------ */
    @PostMapping
    public R<Void> add(@RequestBody RoleSaveVo vo) {
        SysRole r = new SysRole();
        r.setName(vo.getName());
        r.setDesc(vo.getDesc());
        roleService.saveRoleWithPerms(r, vo.getPermIdList());
        return R.ok(null);
    }

    /* ------ 编辑 ------ */
    @PutMapping("/{id}")
    public R<Void> upd(@PathVariable Long id, @RequestBody RoleSaveVo vo) {
        SysRole r = roleService.getById(id);
        if (r == null) return R.error(404, "角色不存在");
        r.setName(vo.getName());
        r.setDesc(vo.getDesc());
        roleService.saveRoleWithPerms(r, vo.getPermIdList());
        return R.ok(null);
    }

    /* ------ 删除 ------ */
    @DeleteMapping("/{id}")
    public R<Void> del(@PathVariable Long id) {
        sysRolePermService.lambdaUpdate()
                        .eq(SysRolePerm::getRoleId, id)
                        .remove();
        roleService.removeById(id);
        return R.ok(null);
    }

    /* ------ 下拉：全部权限 ------ */
    @GetMapping("/perms")
    public R<List<SysPerm>> perms() {
        return R.ok(permService.listAll());
    }

    /* ------ 检查角色名是否已存在 ------ */
    @GetMapping("/exists")
    public R<Boolean> exist(@RequestParam String name){
        boolean exist = roleService.lambdaQuery()
                .eq(SysRole::getName, name.trim())
                .count() > 0;
        return R.ok(exist);
    }

    /* ------ 检查中文描述是否已存在 ------ */
    @GetMapping("/existsDesc")
    public R<Boolean> existDesc(@RequestParam String desc){
        boolean exist = roleService.lambdaQuery()
                .eq(SysRole::getDesc, desc.trim())
                .count() > 0;
        return R.ok(exist);
    }

    /**
     * 当前登录用户已拥有的角色 id 列表（用于前端过滤下拉框）
     */
    @GetMapping("/owned")
    public R<List<Long>> owned(@AuthenticationPrincipal UserDetails userDetails){
        // 根据登录名拿到用户主键
        SysUser user = userService.lambdaQuery()
                .eq(SysUser::getUsername, userDetails.getUsername())
                .one();
        if (user == null) return R.ok(Collections.emptyList());

        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        return R.ok(roleIds);
    }
}