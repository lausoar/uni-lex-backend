package com.unilex.backend.dto;

import lombok.Data;

@Data
public class AuditDto {
    private Integer status;   // 2 通过  3 驳回
    private String approveMsg;
}
