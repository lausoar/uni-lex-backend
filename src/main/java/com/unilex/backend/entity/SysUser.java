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

@Data
@TableName("sys_user")
public class SysUser implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;

    /* ======== 冲突根源消除 ======== */
    private Integer status;   // 数据库字段改名

    @TableField("token_version")  // 对应数据库的token_version字段
    private Integer tokenVersion;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /* ======== Spring Security 接口 ======== */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return status == 1; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /* 关键点：isEnabled 与表字段无关，只返回 status 逻辑 */
    @Override
    public boolean isEnabled() { return status == 1; }
}
// 功能模块：用户实体