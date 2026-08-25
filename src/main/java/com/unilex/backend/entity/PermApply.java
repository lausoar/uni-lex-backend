package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 权限申请实体类
 */
@Data
@TableName("perm_apply")
public class PermApply {
    /** 申请ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 申请人ID */
    private Long applicantId;
    /** 申请的角色ID（历史上字段误命名为 perm_id，实际存的一直是角色ID） */
    private Long roleId;
    /** 申请原因 */
    private String reason;
    /** 申请状态：1-待审，2-通过，3-驳回 */
    private Integer status;   // 1待审 2通过 3驳回
    /** 审批人ID */
    private Long approverId;
    /** 审批意见 */
    private String approveMsg;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
