package com.unilex.backend.service;

import com.unilex.backend.mapper.DashboardMapper;
import com.unilex.backend.vo.DashboardVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardMapper mapper;

    public DashboardVo overview() {
        List<DashboardVo.SankeyNode> nodes = new ArrayList<>();
        List<DashboardVo.SankeyLink> links = new ArrayList<>();
        Map<String, Long> nodeMap = new LinkedHashMap<>();

        List<Map<String, Object>> raw = mapper.sankeyRaw();
        for (Map<String, Object> m : raw) {
            String[] arr = ((String) m.get("path")).split("-");
            long cnt = ((Number) m.get("cnt")).longValue();
            String l1 = arr[0], l2 = arr[1], l3 = arr[2];
            nodeMap.merge(l1, cnt, Long::sum);
            nodeMap.merge(l2, cnt, Long::sum);
            nodeMap.merge(l3, cnt, Long::sum);
            links.add(new DashboardVo.SankeyLink(l1, l2, cnt));
            links.add(new DashboardVo.SankeyLink(l2, l3, cnt));
        }
        nodeMap.forEach((k, v) -> nodes.add(new DashboardVo.SankeyNode(k, v)));
        return DashboardVo.builder()
                .userTotal(mapper.userTotal())
                .dirTotalL1(mapper.dirL1())
                .dirTotalL2(mapper.dirL2())
                .dirTotalL3(mapper.dirL3())
                .termTotal(mapper.termTotal())
                .termPending(mapper.termPending())
                .projectTotal(mapper.projectTotal())
                .dirToday(mapper.dirToday())
                .termToday(mapper.termToday())
                .growth(mapper.growth())
                .ratio(mapper.ratio())
                .creatorRank(mapper.creatorRank())
                .rolePermMatrix(mapper.rolePermMatrix())
                .sankeyNodes(nodes)
                .sankeyLinks(links)
                .projectPending(mapper.projectPending())
                .build();
    }
}