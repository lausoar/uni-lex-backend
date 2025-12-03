package com.unilex.backend.service.serviceImpl;

import com.unilex.backend.entity.CatDirectory;
import com.unilex.backend.entity.TermEntry;
import com.unilex.backend.mapper.CatDirectoryMapper;
import com.unilex.backend.mapper.TermEntryMapper;
import com.unilex.backend.service.DirectoryService;
import com.unilex.backend.vo.DirTreeVo;
import com.unilex.backend.vo.TermRowVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

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
                            List<TermEntry> tList = termMap.getOrDefault(d.getId(), List.of());
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
        return roots;
    }

    private TermRowVo convertTerm(TermEntry e) {
        return TermRowVo.builder()
                .id(e.getId())
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
}