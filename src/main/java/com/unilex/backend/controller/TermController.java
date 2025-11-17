package com.unilex.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unilex.backend.common.R;
import com.unilex.backend.service.TermService;
import com.unilex.backend.vo.TermRowVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/term")
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @GetMapping("/list")
    public R<List<TermRowVo>> list(@RequestParam(required = false) Long dirId) {
        return R.ok(termService.listTerm(dirId));
    }

    @GetMapping("/byLeaf")
    public List<TermRowVo> byLeaf(@RequestParam Long dirId) {
        return termService.listTerm(dirId);   // 直接用现成的实现
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id,
                          @RequestBody TermRowVo vo) {
        termService.updateTerm(id, vo);
        return R.ok(null);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        termService.removeById(id);
        return R.ok(null);
    }
}
