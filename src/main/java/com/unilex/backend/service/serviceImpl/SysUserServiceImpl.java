package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.entity.SysRolePerm;
import com.unilex.backend.entity.SysUser;
import com.unilex.backend.entity.SysUserRole;
import com.unilex.backend.mapper.SysPermMapper;
import com.unilex.backend.mapper.SysUserMapper;
import com.unilex.backend.service.SysRolePermService;
import com.unilex.backend.service.SysUserRoleService;
import com.unilex.backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现
 * <p>提供用户注册、查询、密码修改、角色权限绑定及token版本管理</p>
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleService userRoleService;
    private final SysRolePermService rolePermService;
    private final SysPermMapper permMapper;

    @Override
    public boolean exist(String username) {
        return lambdaQuery().eq(SysUser::getUsername, username).count() > 0;
    }

    @Override
    public SysUser getByUsername(String username) {
        return lambdaQuery().eq(SysUser::getUsername, username).one();
    }

    @Override
    @Transactional
    public void saveUserWithRoleAndPerm(SysUser user,
                                        List<Long> roleIdList,
                                        List<String> permCodeList) {

        /* 1. 用户本身 */
        if (user.getId() == null) {
            save(user);
        } else {
            updateById(user);
            /* 清旧关联 */
            userRoleService.lambdaUpdate()
                    .eq(SysUserRole::getUserId, user.getId())
                    .remove();
//            rolePermService.lambdaUpdate()
//                    .eq(SysRolePerm::getRoleId, user.getId())
//                    .remove();
        }

        /* 2. 用户-角色 */
        if (roleIdList != null && !roleIdList.isEmpty()) {
            List<SysUserRole> urList = roleIdList.stream()
                    .map(rid -> new SysUserRole(user.getId(), rid))
                    .collect(Collectors.toList());
            userRoleService.saveBatch(urList);
        }

        /* 3. 用户-权限 */
        if (permCodeList != null && !permCodeList.isEmpty()) {
            List<Long> permIds = permMapper.selectList(
                            new LambdaQueryWrapper<SysPerm>()
                                    .in(SysPerm::getPermCode, permCodeList))
                    .stream()
                    .map(SysPerm::getId)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            if (!permIds.isEmpty()) {
                List<SysRolePerm> rpList = permIds.stream()
                        .map(pid -> new SysRolePerm(user.getId(), pid))
                        .collect(Collectors.toList());
                rolePermService.saveBatch(rpList);
            }
        }
    }

    @Override
    @Transactional
    public void incrementTokenVersion(String username) {
        UpdateWrapper<SysUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("username", username)
                .setSql("token_version = token_version + 1");
        update(wrapper);
    }

    @Override
    public Integer getTokenVersion(String username) {
        SysUser user = getByUsername(username);
        return user != null ? user.getTokenVersion() : 0;
    }

    /**
     *
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    @Transactional
    public void editPassword(String username, String oldPassword, String newPassword) {
        // 获取用户信息
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }
        // 验证新密码长度
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("新密码至少8位");
        }
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        // 增加token版本，让其他设备下线
        incrementTokenVersion(username);
    }
}
// 功能模块：用户 Service 实现