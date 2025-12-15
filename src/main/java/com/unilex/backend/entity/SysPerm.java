package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPerm {
    @TableId(type = IdType.AUTO)
    private  Integer id;
    private  String permCode;
    private  String permName;
    private  String isSystem;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
