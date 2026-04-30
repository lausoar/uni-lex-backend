package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 登录响应VO，封装登录成功后返回的用户凭证及权限信息。
 */
@Data
@Builder
public class LoginRespVo {
    /** 访问令牌 */
    private String token;
    /** 用户名 */
    private String username;
    /** 用户ID */
    private Long userId;
    /** 权限编码列表 */
    private List<String> perms;   // 权限编码列表
}
