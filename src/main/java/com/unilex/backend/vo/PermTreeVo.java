package com.unilex.backend.vo;

import lombok.Data;
import java.util.List;

@Data
public class PermTreeVo {
    private Long key;      // 权限 id
    private String title;  // 权限名称
    private List<PermTreeVo> children;
}