package com.unilex.backend.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户保存VO，用于新增或编辑用户时提交参数。
 */
@Data
public class UserSaveVo {
    /** 用户名 */
    private String username;
    /** 密码，新增必传，编辑可空 */
    private String password;   // 新增必传，编辑可空
    /** 用户状态，默认1 */
    private Integer status = 1;
    /** 角色ID数组 */
    private List<Long>   roleIdList;   // 角色 id 数组
    /** 权限code数组 */
    private List<String> permCodeList; // 权限 code 数组
}
