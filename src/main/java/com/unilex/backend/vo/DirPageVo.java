package com.unilex.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DirPageVo {
    private Long id;
    private Long parentId;
    private Integer dirType;
    private String dirKey;
    private String dirNameZh;
    private String dirNameEn;
    private Integer sortOrder;
    private Boolean isSystem;
    private LocalDateTime createdAt;
}