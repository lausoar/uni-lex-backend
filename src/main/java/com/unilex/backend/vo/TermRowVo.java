package com.unilex.backend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TermRowVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long creator;
    private String shortKey;
    private String definition;
    private String zhCn;
    private String enUs;
    private String jaJp;
    private String projectName;
    private Boolean productSmartom;
    private Boolean productEms;
    private Boolean productOnepoint;
    private Boolean predefined;
    private Boolean confirmed;
    private Integer sortOrder;
    private Long dirId;
}
