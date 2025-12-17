package com.unilex.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardVo {
    private Long userTotal;          // 用户总数
    private Long dirTotalL1;         // 一级目录
    private Long dirTotalL2;         // 二级目录
    private Long dirTotalL3;         // 三级目录
    private Long termTotal;          // 术语总数
    private Long termPending;        // 待确认
    private Long projectTotal;       // 项目数（去重）
    private Long dirToday;           // 今日新增目录
    private Long termToday;          // 今日新增术语

    private List<TermGrowthRow> growth;   // 月新增 + 累计
    private List<ProductRatioRow> ratio;  // 产品维度启用率
    private List<CreatorRankRow> creatorRank;      // 创建人活跃度 TOP20
    private List<RolePermMatrixRow> rolePermMatrix;// 角色权限矩阵
    private List<SankeyNode> sankeyNodes;          // 桑基图节点
    private List<SankeyLink> sankeyLinks;          // 桑基图边
    private List<ProjectPendingRow> projectPending;// 项目未确认统计


    @Data
    @AllArgsConstructor
    public static class TermGrowthRow {
        private String month;   // yyyy-MM
        private Long newCount;
        private Long totalCount;
    }
    @Data
    @AllArgsConstructor
    public static class ProductRatioRow {
        private String product; // SmartOM / EMS / OnePoint
        private Long enabled;
        private Long disabled;
    }
    @Data
    @AllArgsConstructor
    public static class CreatorRankRow {
        String username;
        Long count;
    }
    @Data
    @AllArgsConstructor
    public static class RolePermMatrixRow {
        String role;
        Long permCount;
    }
    @Data
    @AllArgsConstructor
    public static class ProjectPendingRow {
        String project;
        Long pending;
    }
    @Data
    @AllArgsConstructor
    public static class SankeyNode {
        String name;
        Long value;
    }   // value=术语数
    @Data
    @AllArgsConstructor
    public static class SankeyLink {
        String source;
        String target;
        Long value;
    }
}