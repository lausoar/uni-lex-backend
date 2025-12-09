package com.unilex.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysPerm {
    @TableId(type = IdType.AUTO)
    private  Integer id;
    private  String permCode;
    private  String permName;
    private  String isSystem;
    private LocalDateTime createTime;
}
