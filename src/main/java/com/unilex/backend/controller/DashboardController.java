package com.unilex.backend.controller;

import com.unilex.backend.common.R;
import com.unilex.backend.service.DashboardService;
import com.unilex.backend.vo.DashboardVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;

    @GetMapping("/overview")
    public R<DashboardVo> overview() {
        return R.ok(service.overview());
    }
}
