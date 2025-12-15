package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PermPageVo {
    private Integer id;
    private String permCode;
    private String permName;
    private Boolean isSystem;
    private LocalDateTime createdAt;
}