package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TermRowVo {
    private Long id;
    private String shortKey;
    private String definition;
    private String zhCn;
    private String enUs;
    private String jaJp;
    private Boolean productSmartom;
    private Boolean productEms;
    private Boolean productOnepoint;
    private Boolean predefined;
    private Boolean confirmed;
    private Integer sortOrder;
}
