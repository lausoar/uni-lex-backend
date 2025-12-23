package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    @Transactional
    public void register(String username, String rawPassword) {
        /* 唯一键冲突 DB 会抛 DuplicateKeyException，这里只补长度兜底 */
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("密码长度至少 8 位");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        save(user);
        /* 绑定游客角色（role_id = 3） */
        SysUserRole tourist = new SysUserRole(user.getId(), 3L);
        userRoleService.save(tourist);
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
            rolePermService.lambdaUpdate()
                    .eq(SysRolePerm::getRoleId, user.getId())
                    .remove();
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
}
// 功能模块：用户 Service 实现
