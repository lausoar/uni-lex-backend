package com.unilex.backend.dto;

import lombok.Data;

/**
 * 首页和后台管理页面的导航头上修改密码
 */
@Data
public class EditPasswordDto {
    /** 旧密码 */
    private String oldPassword;
    /** 新密码 */
    private String newPassword;
}
