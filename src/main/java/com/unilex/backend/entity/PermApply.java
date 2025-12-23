package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("perm_apply")
public class PermApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicantId;
    private Long permId;
    private String reason;
    private Integer status;   // 1待审 2通过 3驳回
    private Long approverId;
    private String approveMsg;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}