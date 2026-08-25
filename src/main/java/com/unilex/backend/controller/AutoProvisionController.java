package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.entity.SysUserRole;
import com.unilex.backend.security.OpLog;
import com.unilex.backend.service.SysPermService;
import com.unilex.backend.service.SysRoleService;
import com.unilex.backend.service.SysUserRoleService;
import com.unilex.backend.service.SysUserService;
import com.unilex.backend.utils.PinyinUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auto-provision")
@RequiredArgsConstructor
public class AutoProvisionController {

    /** 初始密码字符集：去掉 I/O/l/0/1 等易混淆字符，方便用户从飞书消息中手动输入 */
    private static final String PASSWORD_ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int PASSWORD_LENGTH = 12;
    /** OpenClaw 回调专用的请求头名 */
    private static final String TOKEN_HEADER = "X-Provision-Token";

    private final SysUserService userService;
    private final SysRoleService roleService;
    private final SysUserRoleService userRoleService;
    private final SysPermService permService;
    private final PasswordEncoder encoder;

    /**
     * OpenClaw 回调共享密钥，通过环境变量 AUTO_PROVISION_SECRET 注入。
     * 未配置时接口保持关闭（fail closed）。
     */
    @Value("${auto-provision.secret:}")
    private String provisionSecret;

    /**
     * OpenClaw调用入口。
     * 无登录态的服务间回调，以共享密钥代替 JWT：请求头 X-Provision-Token 必须与配置一致。
     * 注意：token 通过 HttpServletRequest 读取而非方法参数，避免被 @OpLog 记入操作日志。
     */
    @PostMapping
    @OpLog(module = "user", operation = "CREATE", description = "OpenClaw自动开通账号")
    public R<String> autoProvision(HttpServletRequest request,
                                   @RequestBody Map<String, Object> req) {

        if (provisionSecret == null || provisionSecret.isEmpty()) {
            return R.error(503, "自动开通接口未启用");
        }
        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || !MessageDigest.isEqual(
                provisionSecret.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8))) {
            return R.error(403, "签名校验失败");
        }

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
        String rawPassword = null;

        // 5️⃣ 不存在 → 创建，生成随机初始密码
        if (user == null) {
            rawPassword = generatePassword();
            user = new SysUser();
            user.setUsername(username);
            user.setPassword(encoder.encode(rawPassword));
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

        // 角色已被覆盖，使该用户的权限缓存失效
        permService.evictUserPermCache(username);

        // 8️⃣ 返回结果（给飞书用）
        // 只有新建用户才返回初始密码；老用户密码不会被重置，不能谎报
        StringBuilder resultText = new StringBuilder("用户名：" + username);
        if (rawPassword != null) {
            resultText.append("\n密码：").append(rawPassword);
        }
        resultText.append("\n角色：").append(roleName);
        if (!isNew) {
            resultText.append("\n（账号已存在，角色已更新，密码未变更）");
        }

        return R.ok(resultText.toString());
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
     * 生成随机初始密码
     */
    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_ALPHABET.charAt(random.nextInt(PASSWORD_ALPHABET.length())));
        }
        return sb.toString();
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
