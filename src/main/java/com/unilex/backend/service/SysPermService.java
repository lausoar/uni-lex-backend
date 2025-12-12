package com.unilex.backend.service;

import com.unilex.backend.entity.SysPerm;
import java.util.List;

public interface SysPermService {
    List<String> listUserPerms(String username);
    boolean hasPerm(String username, String permCode);
    List<SysPerm> listAll();
}