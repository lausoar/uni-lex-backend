package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.SysRolePerm;
import com.unilex.backend.mapper.SysRolePermMapper;
import com.unilex.backend.service.SysRolePermService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysRolePermServiceImpl extends ServiceImpl<SysRolePermMapper, SysRolePerm>
        implements SysRolePermService {
}
