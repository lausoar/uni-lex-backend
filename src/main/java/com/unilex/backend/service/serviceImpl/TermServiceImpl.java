package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.mapper.TermEntryMapper;
import com.unilex.backend.service.TermService;
import com.unilex.backend.vo.TermRowVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermServiceImpl extends ServiceImpl<TermEntryMapper, TermEntry> implements TermService {

    private final TermEntryMapper termEntryMapper;

    @Override
    public List<TermRowVo> listTerm(Long dirId) {
        return lambdaQuery()
                .eq(dirId != null, TermEntry::getDirId, dirId)
                .orderByAsc(TermEntry::getSortOrder)
                .list()
                .stream()
                .map(e -> TermRowVo.builder()
                        .id(e.getId())
                        .shortKey(e.getShortKey())
                        .definition(e.getDefinition())
                        .zhCn(e.getZhCn())
                        .enUs(e.getEnUs())
                        .jaJp(e.getJaJp())
                        .productSmartom(e.getProductSmartom() == 1)
                        .productEms(e.getProductEms() == 1)
                        .productOnepoint(e.getProductOnepoint() == 1)
                        .predefined(e.getIsPredefined() == 1)
                        .confirmed(e.getConfirmed() == 1)
                        .sortOrder(e.getSortOrder())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateTerm(Long id, TermRowVo vo) {
        TermEntry entry = new TermEntry();
        entry.setId(id);
        entry.setShortKey(vo.getShortKey());
        entry.setDefinition(vo.getDefinition());
        entry.setZhCn(vo.getZhCn());
        entry.setEnUs(vo.getEnUs());
        entry.setJaJp(vo.getJaJp());
        entry.setProductSmartom(vo.getProductSmartom() ? 1 : 0);
        entry.setProductEms(vo.getProductEms() ? 1 : 0);
        entry.setProductOnepoint(vo.getProductOnepoint() ? 1 : 0);
        termEntryMapper.updateById(entry);
    }
}
