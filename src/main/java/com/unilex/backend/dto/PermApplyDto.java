package com.unilex.backend.dto;

import lombok.Data;

/**
 * 权限申请DTO
 */
@Data
public class PermApplyDto {
    /** 申请的权限ID */
    private Long permId;
    /** 申请原因 */
    private String reason;
}
