package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RolePermPageVo {
    private Long id;
    private String name;   // 角色编码
    private String desc;   // 中文描述
    private List<Long> permIdList; // 已关联权限 id
    private LocalDateTime createdAt;
}