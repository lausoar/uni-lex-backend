package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserPageVo {
    private Long id;
    private String username;
    private Integer status;
    private String role;
    private String roleDesc;
    private List<String> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long>   roleIdList;   // 当前用户角色 id
    private List<String> permCodeList; // 当前用户权限码
}
