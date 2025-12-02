package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.mapper.TermEntryMapper;
import com.unilex.backend.service.TermService;
import com.unilex.backend.vo.TermFlagsUpdateVo;
import com.unilex.backend.vo.TermRowVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    @Override
    @Transactional
    public TermEntry addTerm(TermRowVo vo) {
        // 1. 构造实体
        TermEntry entry = new TermEntry();
        entry.setDirId(vo.getDirId());
        entry.setShortKey(vo.getShortKey());
        entry.setDefinition(vo.getDefinition());
        entry.setZhCn(vo.getZhCn());
        entry.setEnUs(vo.getEnUs());
        entry.setJaJp(vo.getJaJp());
        entry.setProductSmartom(boolToInt(vo.getProductSmartom()));
        entry.setProductEms(boolToInt(vo.getProductEms()));
        entry.setProductOnepoint(boolToInt(vo.getProductOnepoint()));

        // 2. 默认值
        entry.setIsPredefined(0);
        entry.setConfirmed(0);

        /* ===== 关键：本目录最小 sort_order - 1 ===== */
        Integer minSort = Optional.ofNullable(
                lambdaQuery()
                        .eq(TermEntry::getDirId, vo.getDirId())
                        .orderByAsc(TermEntry::getSortOrder)
                        .last("LIMIT 1")
                        .one()
        ).map(TermEntry::getSortOrder).orElse(1);

        entry.setSortOrder(minSort - 1);

        // 3. 落库
        save(entry);
        return entry;
    }

    @Override
    @Transactional
    public void updateFlags(Long id, TermFlagsUpdateVo vo) {
        TermEntry e = new TermEntry();
        e.setId(id);
        if (vo.getConfirmed() != null) {
            e.setConfirmed(vo.getConfirmed() ? 1 : 0);
        }
        if (vo.getPredefined() != null) {
            e.setIsPredefined(vo.getPredefined() ? 1 : 0);
        }
        // 只更新非 null 字段
        termEntryMapper.updateById(e);
    }

    @Override
    public List<TermRowVo> search(List<String> products, String dataType, String keyword) {
        LambdaQueryWrapper<TermEntry> qw = new LambdaQueryWrapper<>();

        /* 1. 关键字模糊查询：shortKey / 中文 / 英文 / 日文 任一匹配 */
        if (StringUtils.isNotBlank(keyword)) {
            qw.and(w -> w
                    .like(TermEntry::getShortKey, keyword)
                    .or()
                    .like(TermEntry::getZhCn, keyword)
                    .or()
                    .like(TermEntry::getEnUs, keyword)
                    .or()
                    .like(TermEntry::getJaJp, keyword));
        }

        /* 2. 多选产品 → AND 关系（所有选中都必须为 1） */
        if (products != null && !products.isEmpty()) {
            qw.and(w -> {
                for (String p : products) {
                    if ("SmartOM".equalsIgnoreCase(p)) w.eq(TermEntry::getProductSmartom, 1);
                    if ("EMS".equalsIgnoreCase(p)) w.eq(TermEntry::getProductEms, 1);
                    if ("OnePoint".equalsIgnoreCase(p)) w.eq(TermEntry::getProductOnepoint, 1);
                }
            });
        }

        /* 3. 数据类型过滤（示例：用 shortKey 前缀 like 模拟） */
        if (StringUtils.isNotBlank(dataType)) {
            qw.eq(TermEntry::getDirId, dataType);
        }

        qw.orderByAsc(TermEntry::getSortOrder);
        return list(qw).stream().map(this::convert).collect(Collectors.toList());
    }
    /* ---------------- 私有工具 ---------------- */
    private int boolToInt(Boolean b) {
        return Boolean.TRUE.equals(b) ? 1 : 0;
    }

    private TermRowVo convert(TermEntry e) {
        return TermRowVo.builder()
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
                .dirId(e.getDirId())
                .build();
    }
}
