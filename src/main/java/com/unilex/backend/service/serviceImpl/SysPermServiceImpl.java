package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysPerm;
import com.unilex.backend.mapper.SysPermMapper;
import com.unilex.backend.service.SysPermService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统权限服务实现
 * <p>提供用户权限查询（带缓存）、权限校验及权限维护功能</p>
 */
@Service
@RequiredArgsConstructor
public class SysPermServiceImpl extends ServiceImpl<SysPermMapper, SysPerm> implements SysPermService{

    private final SysPermMapper permMapper;

    /*
     * 两个方法的缓存 key 必须带后缀区分：
     * 原来都用 key="#username"，后调用者会拿到先缓存者的错误类型数据
     * （权限码列表与权限名称列表串味，hasPerm 会拿权限名称做比对，属授权隐患）
     */
    @Override
    @Cacheable(value = "perm", key = "#username + ':codes'")
    public List<String> listUserPerms(String username) {
        return permMapper.listCodesByUsername(username);
    }

    @Override
    @Cacheable(value = "perm", key = "#username + ':names'")
    public List<String> listUserPermsName(String username) {
        return permMapper.listNamesByUsername(username);
    }

    @Override
    public boolean hasPerm(String username, String permCode) {
        return listUserPerms(username).contains(permCode);
    }

    // 新增：查全部权限
    public List<SysPerm> listAll() {
        return list();
    }

    /*
     * 注意：@CacheEvict 必须直接加在本方法上，不能在方法体内自调用
     * evictAllPermCache()——同类内部自调用不经过 AOP 代理，注解不会生效。
     */
    @Override
    @Transactional
    @CacheEvict(value = "perm", allEntries = true)
    public void savePerm(SysPerm po) {
        // 唯一校验：permCode 不能重复
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

    @Override
    @Caching(evict = {
            @CacheEvict(value = "perm", key = "#username + ':codes'"),
            @CacheEvict(value = "perm", key = "#username + ':names'")
    })
    public void evictUserPermCache(String username) {
        // 注解驱动，无需方法体
    }

    @Override
    @CacheEvict(value = "perm", allEntries = true)
    public void evictAllPermCache() {
        // 注解驱动，无需方法体
    }
}