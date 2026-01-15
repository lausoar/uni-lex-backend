package com.unilex.backend.dto;

import lombok.Data;

/**
 * 首页和后台管理页面的导航头上修改密码
 */
@Data
public class EditPasswordDto {
    private String oldPassword;
    private String newPassword;
}
