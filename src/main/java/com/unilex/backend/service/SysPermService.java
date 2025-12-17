package com.unilex.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.SysPerm;
import java.util.List;

public interface SysPermService extends IService<SysPerm> {
    List<String> listUserPerms(String username);
    List<String> listUserPermsName(String username);
    boolean hasPerm(String username, String permCode);
    List<SysPerm> listAll();
    void savePerm(SysPerm po);
}