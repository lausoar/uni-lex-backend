package com.unilex.backend.vo;

import lombok.Data;
import java.util.List;

/**
 * 角色保存VO，用于新增或编辑角色时提交参数。
 */
@Data
public class RoleSaveVo {
    /** 角色编码 */
    private String name;
    /** 中文描述 */
    private String desc;
    /** 权限ID集合 */
    private List<Long> permIdList; // 权限 id 集合
}
