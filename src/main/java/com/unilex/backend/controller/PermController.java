package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unilex.backend.common.R;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.entity.SysRolePerm;
import com.unilex.backend.security.ReqPerm;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysRolePermService;
import com.unilex.backend.vo.PermPageVo;
import com.unilex.backend.vo.PermSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/perm")
@RequiredArgsConstructor
public class PermController {

    private final SysPermService permService;
    private final SysRolePermService rolePermService;

    /* ------ 分页+搜索 ------ */
    @GetMapping
    @ReqPerm("perm:manage")
    public R<Page<PermPageVo>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<SysPerm> poPage = permService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<SysPerm>()
                        .and(keyword != null,
                                q -> q.like(SysPerm::getPermCode, keyword)
                                        .or()
                                        .like(SysPerm::getPermName, keyword))
                        .orderByAsc(SysPerm::getId)
        );

        Page<PermPageVo> voPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        List<PermPageVo> records = poPage.getRecords().stream()
                .map(p -> PermPageVo.builder()
                        .id(p.getId())
                        .permCode(p.getPermCode())
                        .permName(p.getPermName())
                        .isSystem("1".equals(p.getIsSystem()))
                        .createdAt(p.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        voPage.setRecords(records);
        return R.ok(voPage);
    }

    /* ------ 新增 ------ */
    @PostMapping
    @ReqPerm("perm:manage")
    public R<Void> add(@RequestBody PermSaveVo vo) {
        SysPerm po = new SysPerm();
        po.setPermCode(vo.getPermCode());
        po.setPermName(vo.getPermName());
        po.setIsSystem(vo.getIsSystem() != null && vo.getIsSystem() ? "1" : "0");
        permService.savePerm(po);
        return R.ok(null);
    }

    /* ------ 编辑 ------ */
    @PutMapping("/{id}")
    @ReqPerm("perm:manage")
    public R<Void> upd(@PathVariable Integer id, @RequestBody PermSaveVo vo) {
        SysPerm po = permService.getById(id);
        if (po == null) return R.error(404, "权限不存在");
        po.setPermCode(vo.getPermCode());
        po.setPermName(vo.getPermName());
        po.setIsSystem(vo.getIsSystem() != null && vo.getIsSystem() ? "1" : "0");
        permService.savePerm(po);
        return R.ok(null);
    }

    /* ------ 删除 ------ */
    @DeleteMapping("/{id}")
    @ReqPerm("perm:manage")
    public R<Void> del(@PathVariable Integer id) {
        rolePermService.lambdaUpdate()
                        .eq(SysRolePerm::getPermId, id)
                        .remove();
        permService.removeById(id);
        // 权限点删除影响拥有该权限的所有用户
        permService.evictAllPermCache();
        return R.ok(null);
    }

    /* ------ 下拉：全部权限 ------ */
    @GetMapping("/all")
    @ReqPerm("perm:manage")
    public R<List<SysPerm>> all() {
        return R.ok(permService.list());
    }

    /* ------ 校验权限编码是否已存在 ------ */
    @GetMapping("/exist-code")
    @ReqPerm("perm:manage")
    public R<Boolean> existCode(@RequestParam String code,
                                @RequestParam(required = false) Integer excludeId) {
        boolean exist = permService.lambdaQuery()
                .eq(SysPerm::getPermCode, code)
                .ne(excludeId != null, SysPerm::getId, excludeId)
                .count() > 0;
        return R.ok(exist);
    }

    /* ------ 校验权限名称是否已存在 ------ */
    @GetMapping("/exist-name")
    @ReqPerm("perm:manage")
    public R<Boolean> existName(@RequestParam String name,
                                @RequestParam(required = false) Integer excludeId) {
        boolean exist = permService.lambdaQuery()
                .eq(SysPerm::getPermName, name)
                .ne(excludeId != null, SysPerm::getId, excludeId)
                .count() > 0;
        return R.ok(exist);
    }
}