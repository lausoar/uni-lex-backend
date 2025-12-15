package com.unilex.backend.vo;

import lombok.Data;

@Data
public class PermSaveVo {
    private String permCode;
    private String permName;
    private Boolean isSystem;
}