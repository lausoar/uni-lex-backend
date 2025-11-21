package com.unilex.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.vo.TermFlagsUpdateVo;
import com.unilex.backend.vo.TermRowVo;

import java.util.List;

public interface TermService extends IService<TermEntry> {
    /**
     * 查询某个叶子目录下的术语列表（分页）
     */
    List<TermRowVo> listTerm(Long dirId);

    void updateTerm(Long id, TermRowVo vo);

    TermEntry addTerm(TermRowVo vo);

    void updateFlags(Long id, TermFlagsUpdateVo vo);
}
