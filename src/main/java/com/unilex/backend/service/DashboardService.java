package com.unilex.backend.service;

import com.unilex.backend.mapper.DashboardMapper;
import com.unilex.backend.vo.DashboardVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘数据服务
 * <p>汇总系统各类统计数据并组装为桑基图等可视化结构</p>
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardMapper mapper;

    /**
     * 获取仪表盘概览数据
     * @return 包含统计数据、桑基图、排名等信息的仪表盘VO
     */
    public DashboardVo overview() {
        List<DashboardVo.SankeyNode> nodes = new ArrayList<>();
        List<DashboardVo.SankeyLink> links = new ArrayList<>();
        Map<String, Long> nodeMap = new LinkedHashMap<>();

        /* 1. 查询原始路径数据并拆分为三级节点 */
        List<Map<String, Object>> raw = mapper.sankeyRaw();
        for (Map<String, Object> m : raw) {
            String[] arr = ((String) m.get("path")).split("-");
            long cnt = ((Number) m.get("cnt")).longValue();
            String l1 = arr[0], l2 = arr[1], l3 = arr[2];
            /* 累计每个节点的流量 */
            nodeMap.merge(l1, cnt, Long::sum);
            nodeMap.merge(l2, cnt, Long::sum);
            nodeMap.merge(l3, cnt, Long::sum);
            /* 构建两级流向关系 */
            links.add(new DashboardVo.SankeyLink(l1, l2, cnt));
            links.add(new DashboardVo.SankeyLink(l2, l3, cnt));
        }
        /* 2. 节点列表按出现顺序生成 */
        nodeMap.forEach((k, v) -> nodes.add(new DashboardVo.SankeyNode(k, v)));
        /* 3. 汇总所有统计指标并返回 */
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