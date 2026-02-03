package com.unilex.backend.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.mapper.CatDirectoryMapper;
import com.unilex.backend.mapper.TermEntryMapper;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.vo.DirAddVo;
import com.unilex.backend.vo.DirSaveVo;
import com.unilex.backend.vo.DirTreeVo;
import com.unilex.backend.vo.TermRowVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl extends ServiceImpl<CatDirectoryMapper, CatDirectory> implements DirectoryService {

    private final CatDirectoryMapper dirMapper;
    private final TermEntryMapper termMapper;

    @Override
    public List<DirTreeVo> wholeTree(String lang) {
        /* 1. 全部目录  2. 全部术语 */
        List<CatDirectory> dirs = dirMapper.listAllOrdered();
        List<TermEntry> terms = termMapper.selectList(null);
        log.info(">>> dirs={}, terms={}", dirs.size(), terms.size());

        Map<Long, List<TermEntry>> termMap = terms.stream()
                .collect(Collectors.groupingBy(TermEntry::getDirId));

        /* 3. 构造 VO */
        Map<Long, DirTreeVo> voMap = dirs.stream()
                .collect(Collectors.toMap(CatDirectory::getId,
                        d -> {
                            DirTreeVo v = new DirTreeVo();
                            v.setId(d.getId());
                            v.setParentId(d.getParentId());
                            v.setDirKey(d.getDirKey());
                            v.setDirType(d.getDirType());
                            v.setSortOrder(d.getSortOrder());
                            v.setIsSystem(d.getIsSystem());
                            v.setName("zh".equals(lang) ? d.getDirNameZh() : d.getDirNameEn());
                            v.setChildren(new ArrayList<>());
                            /*  关键：只要有术语就挂 termList，不管几级 */
                            List<TermEntry> tList = termMap.getOrDefault(d.getId(), Collections.emptyList());
                            if (!tList.isEmpty()) {
                                v.setTermList(tList.stream()
                                        .sorted(Comparator.comparingInt(TermEntry::getSortOrder))
                                        .map(this::convertTerm)
                                        .collect(Collectors.toList()));
                            }
                            return v;
                        }));

        /* 4. 拼树 */
        List<DirTreeVo> roots = new ArrayList<>();
        voMap.values().forEach(n -> {
            if (n.getParentId() == 0L) roots.add(n);
            else {
                DirTreeVo p = voMap.get(n.getParentId());
                if (p != null) p.getChildren().add(n);
            }
        });

        // 对根目录和每个父目录的子目录按 sort_order 排序
        roots.sort(Comparator.comparingInt(DirTreeVo::getSortOrder));
        voMap.values().forEach(vo ->
                vo.getChildren().sort(Comparator.comparingInt(DirTreeVo::getSortOrder))
        );
        return roots;
    }

    private TermRowVo convertTerm(TermEntry e) {
        return TermRowVo.builder()
                .id(e.getId())
                .creator(e.getCreator())
                .shortKey(e.getShortKey())
                .definition(e.getDefinition())
                .zhCn(e.getZhCn())
                .enUs(e.getEnUs())
                .jaJp(e.getJaJp())
                .projectName(e.getProjectName())
                .productSmartom(e.getProductSmartom() == 1)
                .productEms(e.getProductEms() == 1)
                .productOnepoint(e.getProductOnepoint() == 1)
                .predefined(e.getIsPredefined() == 1)
                .confirmed(e.getConfirmed() == 1)
                .sortOrder(e.getSortOrder())
                .build();
    }

    @Override
    @Transactional
    public Long addDir(DirAddVo vo) {
        /* 1. 基本字段 */
        CatDirectory po = new CatDirectory();
        po.setDirNameZh(vo.getName());
        po.setDirNameEn(vo.getName());
        po.setDirKey(vo.getName()); // 建议唯一
        po.setDirType(vo.getLevel());                 // 0-普通 1-系统 等，你原来逻辑
        po.setIsSystem(0);

        /* 2. 计算 parentId */
        switch (vo.getLevel()) {
            case 1: po.setParentId(0L); break;
            case 2: po.setParentId(vo.getLevel1Id()); break;
            case 3: po.setParentId(vo.getLevel2Id()); break;
            default: throw new IllegalArgumentException("level 只支持 1/2/3");
        }

        /* 3. 计算 sort_order = max+1 或 1 */
        List<CatDirectory> list = lambdaQuery()
                .eq(CatDirectory::getParentId, po.getParentId())
                .eq(CatDirectory::getDirType, po.getDirType())
                .orderByDesc(CatDirectory::getSortOrder)
                .last("LIMIT 1")
                .list();

        Integer maxSort = list.isEmpty() ? 0 : list.get(0).getSortOrder();
        po.setSortOrder(maxSort + 1);

        /* 4. 落库 */
        dirMapper.insert(po);
        return po.getId();
    }

    @Override
    @Transactional
    public void saveDir(DirSaveVo vo) {
        // 唯一校验：同级 dirKey 不能重复
        if (lambdaQuery()
                .eq(CatDirectory::getParentId, vo.getParentId())
                .eq(CatDirectory::getDirKey, vo.getDirKey())
                .ne(vo.getId() != null, CatDirectory::getId, vo.getId())
                .count() > 0) {
            throw new IllegalArgumentException("同级目录下 Key 已存在");
        }
        CatDirectory po = new CatDirectory();
        po.setParentId(vo.getParentId());
        po.setDirType(vo.getDirType());
        po.setDirKey(vo.getDirKey());
        po.setDirNameZh(vo.getDirNameZh());
        po.setDirNameEn(vo.getDirNameEn());
        po.setSortOrder(vo.getSortOrder() == null ? 0 : vo.getSortOrder());
        po.setIsSystem(Boolean.TRUE.equals(vo.getIsSystem()) ? 1 : 0);

        if (vo.getId() == null) {
            save(po);
        } else {
            po.setId(vo.getId());
            updateById(po);
        }
    }

    @Override
    @Transactional
    public void delDir(Long id) {
        // 存在子级禁止删除
        if (lambdaQuery().eq(CatDirectory::getParentId, id).count() > 0) {
            throw new IllegalArgumentException("存在子目录，不允许删除");
        }
        removeById(id);
    }


    @Override
    @Transactional
    public Long addDirWithSort(DirAddVo vo, Integer targetSortOrder) {
        /* 1. 基本字段 */
        CatDirectory po = new CatDirectory();
        po.setDirNameZh(vo.getName());
        po.setDirNameEn(vo.getName());
        po.setDirKey(vo.getName());
        po.setDirType(vo.getLevel());
        po.setIsSystem(0);

        /* 2. 计算 parentId */
        switch (vo.getLevel()) {
            case 1: po.setParentId(0L); break;
            case 2: po.setParentId(vo.getLevel1Id()); break;
            case 3: po.setParentId(vo.getLevel2Id()); break;
            default: throw new IllegalArgumentException("level 只支持 1/2/3");
        }

        /* 3. 处理排序 - 关键改进 */
        Integer finalSortOrder;
        if (targetSortOrder != null) {
            // 如果指定了目标位置，将该位置及之后的所有目录排序+1
            shiftSortOrder(po.getParentId(), po.getDirType(), targetSortOrder);
            finalSortOrder = targetSortOrder;
        } else {
            // 默认添加到最后
            List<CatDirectory> list = lambdaQuery()
                    .eq(CatDirectory::getParentId, po.getParentId())
                    .eq(CatDirectory::getDirType, po.getDirType())
                    .orderByDesc(CatDirectory::getSortOrder)
                    .last("LIMIT 1")
                    .list();
            Integer maxSort = list.isEmpty() ? 0 : list.get(0).getSortOrder();
            finalSortOrder = maxSort + 1;
        }
        po.setSortOrder(finalSortOrder);

        /* 4. 落库 */
        dirMapper.insert(po);
        return po.getId();
    }

    /**
     * 将指定位置及之后的目录排序号+1，为新目录腾出位置
     */
    private void shiftSortOrder(Long parentId, Integer dirType, Integer startSortOrder) {
        lambdaUpdate()
                .eq(CatDirectory::getParentId, parentId)
                .eq(CatDirectory::getDirType, dirType)
                .ge(CatDirectory::getSortOrder, startSortOrder)
                .setSql("sort_order = sort_order + 1")
                .update();
    }

    /**
     * 批量更新目录排序
     */
    @Override
    @Transactional
    public void batchUpdateSort(List<DirSaveVo> sortList) {
        if (sortList == null || sortList.isEmpty()) return;

        for (DirSaveVo vo : sortList) {
            CatDirectory po = new CatDirectory();
            po.setId(vo.getId());
            po.setSortOrder(vo.getSortOrder());
            updateById(po);
        }
    }
}