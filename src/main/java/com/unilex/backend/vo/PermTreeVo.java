package com.unilex.backend.vo;

import lombok.Data;
import java.util.List;

/**
 * 权限树VO，用于构建权限层级树结构。
 */
@Data
public class PermTreeVo {
    /** 权限ID */
    private Long key;      // 权限 id
    /** 权限名称 */
    private String title;  // 权限名称
    /** 子权限列表 */
    private List<PermTreeVo> children;
}
