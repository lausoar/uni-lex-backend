package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class LoginRespVo {
    private String token;
    private String username;
    private Long userId;
    private List<String> perms;   // 权限编码列表
}