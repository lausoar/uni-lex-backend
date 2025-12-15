package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.mapper.SysPermMapper;
import com.unilex.backend.mapper.SysUserMapper;
import com.unilex.backend.service.SysPermService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysPermServiceImpl extends ServiceImpl<SysPermMapper, SysPerm> implements SysPermService{

    private final SysPermMapper permMapper;
    private final SysUserMapper userMapper;

    @Override
    @Cacheable(value = "perm", key = "#username")
    public List<String> listUserPerms(String username) {
        return permMapper.listCodesByUsername(username);
    }

    @Override
    public boolean hasPerm(String username, String permCode) {
        return listUserPerms(username).contains(permCode);
    }

    // 新增：查全部权限
    public List<SysPerm> listAll() {
        return list();
    }

    @Override
    @Transactional
    public void savePerm(SysPerm po) {
        // 唯一校验
        if (lambdaQuery().eq(SysPerm::getPermCode, po.getPermCode())
                .ne(po.getId() != null, SysPerm::getId, po.getId())
                .count() > 0) {
            throw new IllegalArgumentException("权限编码已存在");
        }
        if (po.getId() == null) {
            save(po);
        } else {
            updateById(po);
        }
    }
}