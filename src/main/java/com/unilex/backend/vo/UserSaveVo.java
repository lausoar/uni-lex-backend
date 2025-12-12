package com.unilex.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserSaveVo {
    private String username;
    private String password;   // 新增必传，编辑可空
    private Integer status = 1;
    private List<Long>   roleIdList;   // 角色 id 数组
    private List<String> permCodeList; // 权限 code 数组
}
