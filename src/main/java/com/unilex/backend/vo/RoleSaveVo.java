package com.unilex.backend.vo;

import lombok.Data;
import java.util.List;

@Data
public class RoleSaveVo {
    private String name;
    private String desc;
    private List<Long> permIdList; // 权限 id 集合
}