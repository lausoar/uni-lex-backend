package com.unilex.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermApplyVo {
    private Long id;
    private String applicantName;
    private String permDesc;
    private String reason;
    private Integer status;
    private String approveMsg;
    private LocalDateTime createdAt;
}
