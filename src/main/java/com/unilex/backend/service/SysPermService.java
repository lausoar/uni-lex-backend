package com.unilex.backend.service;

import org.springframework.stereotype.Service;
import java.util.List;

public interface SysPermService {
    List<String> listUserPerms(String username);
    boolean hasPerm(String username, String permCode);
}