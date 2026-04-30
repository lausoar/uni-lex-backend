package com.unilex.backend.dto;

import lombok.Data;

/**
 * 权限审批DTO
 */
@Data
public class AuditDto {
    /** 审批状态：2-通过，3-驳回 */
    private Integer status;   // 2 通过  3 驳回
    /** 审批意见 */
    private String approveMsg;
}
