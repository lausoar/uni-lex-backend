package com.unilex.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * 术语搜索请求DTO
 */
@Data
public class TermSearchRequest {
    /** 产品列表 */
    private List<String> products;
    /** 数据类型 */
    private String dataType;
    /** 搜索关键字 */
    private String keyword;
    /** 项目列表 */
    private List<String> projects;
    /** 确认状态 */
    private String confirm;
}
