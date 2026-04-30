package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysRole;
import com.unilex.backend.mapper.SysRoleMapper;
import com.unilex.backend.service.SysRolePermService;
import com.unilex.backend.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色服务实现
 * <p>提供角色查询、用户角色关联及角色权限绑定功能</p>
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private SysRolePermService rolePermService;
    // 新增：查全部角色
    public List<SysRole> listAll() {
        return list();
    }

    @Override
    public List<SysRole> listByUserId(Long userId) {
        return getBaseMapper().listByUserId(userId);
    }

    @Override
    @Transactional
    public void saveRoleWithPerms(SysRole role, List<Long> permIdList) {
        // 1. 写角色
        if (role.getId() == null) {
            save(role);
        } else {
            updateById(role);
            // 清旧权限
//            rolePermService.lambdaUpdate()
//                    .eq(SysRolePerm::getRoleId, role.getId())
//                    .remove();
        }
        // 2. 写权限
//        if (permIdList != null && !permIdList.isEmpty()) {
//            List<SysRolePerm> list = permIdList.stream()
//                    .map(pid -> new SysRolePerm(role.getId(), pid))
//                    .collect(Collectors.toList());
//            rolePermService.saveBatch(list);
//        }
    }
}