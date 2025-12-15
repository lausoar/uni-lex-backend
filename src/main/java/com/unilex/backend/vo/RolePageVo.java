package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RolePageVo {
    private Long id;
    private String name;   // 角色编码，如 ROLE_ADMIN
    private String desc;   // 中文描述
    private List<String> permList; // 权限编码集合
    private LocalDateTime createdAt;
}