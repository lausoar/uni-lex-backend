package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.entity.SysUserRole;
import com.unilex.backend.service.SysRoleService;
import com.unilex.backend.service.SysUserRoleService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.utils.PinyinUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auto-provision")
@RequiredArgsConstructor
public class AutoProvisionController {

    private final SysUserService userService;
    private final SysRoleService roleService;
    private final SysUserRoleService userRoleService;
    private final PasswordEncoder encoder;

    /**
     * OpenClaw调用入口
     */
    @PostMapping
    public R<String> autoProvision(@RequestBody Map<String, Object> req) {

        String name = (String) req.get("name");              // 张三
        String permission = (String) req.get("permission");  // 管理员/编辑/只读

        if (name == null || name.trim().isEmpty()) {
            return R.error(400, "姓名不能为空");
        }

        // 1️⃣ 中文 → 拼音
        String username = PinyinUtil.toPinyin(name);

        // 2️⃣ 处理重名（防冲突🔥）
        username = generateUniqueUsername(username);

        // 3️⃣ 权限映射
        String roleName = mapRole(permission);

        // 4️⃣ 查用户
        SysUser user = userService.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();

        boolean isNew = false;

        // 5️⃣ 不存在 → 创建
        if (user == null) {
            user = new SysUser();
            user.setUsername(username);
            user.setPassword(encoder.encode("taoke123")); // 默认密码
            user.setStatus(1);
            userService.save(user);
            isNew = true;
        }

        // 6️⃣ 查角色
        List<SysRole> roles = roleService.lambdaQuery()
                .eq(SysRole::getName, roleName)
                .list();

        if (roles.isEmpty()) {
            return R.error(500, "角色不存在：" + roleName);
        }

        List<Long> roleIds = roles.stream()
                .map(SysRole::getId)
                .collect(Collectors.toList());

        // 7️⃣ 覆盖用户角色
        userRoleService.lambdaUpdate()
                .eq(SysUserRole::getUserId, user.getId())
                .remove();

        for (Long rid : roleIds) {
            SysUserRole ur = new SysUserRole(user.getId(), rid);
            ur.setUserId(user.getId());
            ur.setRoleId(rid);
            userRoleService.save(ur);
        }

        // 8️⃣ 返回结果（给飞书用）
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", "taoke123");
        result.put("role", roleName);
        result.put("isNew", isNew);

        String resultText = "用户名：" + username + "\n密码：" + "taoke123";

        return R.ok(resultText);
    }

    /**
     * 权限映射：问卷 → 系统角色
     */
    private String mapRole(String permission) {
        if (permission == null) return "VISTOR";

        switch (permission.trim()) {
            case "管理员":
                return "ADMIN";
            case "编辑":
                return "EDITOR";
            case "只读":
                return "VISTOR";
            default:
                return "VISTOR";
        }
    }

    /**
     * 生成唯一用户名（防止重名）
     */
    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;

        Long count = userService.lambdaQuery()
                .likeRight(SysUser::getUsername, baseUsername)
                .count();

        if (count > 0) {
            username = baseUsername + (count + 1);
        }

        return username;
    }
}