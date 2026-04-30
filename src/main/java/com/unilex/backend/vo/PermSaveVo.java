package com.unilex.backend.vo;

import lombok.Data;

/**
 * 权限保存VO，用于新增或编辑权限时提交参数。
 */
@Data
public class PermSaveVo {
    /** 权限编码 */
    private String permCode;
    /** 权限名称 */
    private String permName;
    /** 是否系统内置 */
    private Boolean isSystem;
}
