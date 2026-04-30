package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 系统用户实体类，实现Spring Security的UserDetails接口
 */
@Data
@TableName("sys_user")
public class SysUser implements UserDetails {

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;

    /* ======== 冲突根源消除 ======== */
    /** 用户状态 */
    private Integer status;   // 数据库字段改名

    /** Token版本号，对应数据库的token_version字段 */
    @TableField("token_version")  // 对应数据库的token_version字段
    private Integer tokenVersion;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /* ======== Spring Security 接口 ======== */
    /**
     * 获取用户权限集合
     * @return 权限集合，默认返回ROLE_USER角色
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    /**
     * 判断账户是否未过期
     * @return 始终返回true
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * 判断账户是否未锁定
     * @return 用户状态为1时返回true
     */
    @Override
    public boolean isAccountNonLocked() { return status == 1; }

    /**
     * 判断凭证是否未过期
     * @return 始终返回true
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /* 关键点：isEnabled 与表字段无关，只返回 status 逻辑 */
    /**
     * 判断用户是否启用
     * @return 用户状态为1时返回true
     */
    @Override
    public boolean isEnabled() { return status == 1; }
}
// 功能模块：用户实体
