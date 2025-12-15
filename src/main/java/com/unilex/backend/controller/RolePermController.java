package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysRolePerm;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysRolePermService;
import com.unilex.backend.service.SysRoleService;
import com.unilex.backend.vo.PermTreeVo;
import com.unilex.backend.vo.RolePermPageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/role-perm")
@RequiredArgsConstructor
public class RolePermController {

    private final SysRoleService roleService;
    private final SysPermService permService;
    private final SysRolePermService rolePermService;

    /* 1. 角色分页（含已有权限 id 列表） */
    @GetMapping("/roles")
    public R<Page<RolePermPageVo>> roles(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<SysRole> poPage = roleService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<SysRole>()
                        .and(keyword != null,
                                q -> q.like(SysRole::getName, keyword)
                                        .or()
                                        .like(SysRole::getDesc, keyword))
                        .orderByAsc(SysRole::getId)
        );

        Page<RolePermPageVo> voPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        List<Long> roleIds = poPage.getRecords().stream().map(SysRole::getId).collect(Collectors.toList());
        Map<Long, List<Long>> rolePermMap = rolePermService.lambdaQuery()
                .in(SysRolePerm::getRoleId, roleIds)
                .list()
                .stream()
                .collect(Collectors.groupingBy(SysRolePerm::getRoleId,
                        Collectors.mapping(SysRolePerm::getPermId, Collectors.toList())));

        List<RolePermPageVo> records = poPage.getRecords().stream().map(r ->
                RolePermPageVo.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .desc(r.getDesc())
                        .permIdList(rolePermMap.getOrDefault(r.getId(), List.of()))
                        .createdAt(LocalDateTime.now()) // 表无字段，mock
                        .build()
        ).collect(Collectors.toList());
        voPage.setRecords(records);
        return R.ok(voPage);
    }

    /* 2. 权限树（一次性查出，前端自行拼父子） */
    @GetMapping("/perms")
    public R<List<PermTreeVo>> perms() {
        List<SysPerm> list = permService.list();
        List<PermTreeVo> tree = list.stream()
                .map(p -> {
                    PermTreeVo vo = new PermTreeVo();
                    vo.setKey(Long.valueOf(p.getId()));
                    vo.setTitle(p.getPermName() + " (" + p.getPermCode() + ")");
                    vo.setChildren(null); // 平级，可扩展父子
                    return vo;
                }).collect(Collectors.toList());
        return R.ok(tree);
    }

    /* 3. 保存角色权限 */
    @PutMapping("/{roleId}")
    public R<Void> savePerms(@PathVariable Long roleId, @RequestBody List<Long> permIdList) {
        rolePermService.lambdaUpdate().eq(SysRolePerm::getRoleId, roleId).remove();
        if (permIdList != null && !permIdList.isEmpty()) {
            List<SysRolePerm> list = permIdList.stream()
                    .map(pid -> new SysRolePerm(roleId, pid))
                    .collect(Collectors.toList());
            rolePermService.saveBatch(list);
        }
        return R.ok(null);
    }
}