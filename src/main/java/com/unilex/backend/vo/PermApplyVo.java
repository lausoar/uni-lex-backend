package com.unilex.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限申请VO，用于展示权限申请记录详情。
 */
@Data
public class PermApplyVo {
    /** 申请ID */
    private Long id;
    /** 申请人姓名 */
    private String applicantName;
    /** 申请角色描述 */
    private String roleDesc;
    /** 申请理由 */
    private String reason;
    /** 申请状态 */
    private Integer status;
    /** 审批意见 */
    private String approveMsg;
    /** 申请创建时间 */
    private LocalDateTime createdAt;
    /** 申请更新时间 */
    private LocalDateTime updatedAt;
    /** 审批人姓名 */
    private String approverName;
}
