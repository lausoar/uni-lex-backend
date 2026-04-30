package com.unilex.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 仪表盘数据VO，汇总系统各项统计指标及图表数据。
 */
@Data
@Builder
public class DashboardVo {
    /** 用户总数 */
    private Long userTotal;          // 用户总数
    /** 一级目录总数 */
    private Long dirTotalL1;         // 一级目录
    /** 二级目录总数 */
    private Long dirTotalL2;         // 二级目录
    /** 三级目录总数 */
    private Long dirTotalL3;         // 三级目录
    /** 术语总数 */
    private Long termTotal;          // 术语总数
    /** 待确认术语数 */
    private Long termPending;        // 待确认
    /** 项目数（去重） */
    private Long projectTotal;       // 项目数（去重）
    /** 今日新增目录数 */
    private Long dirToday;           // 今日新增目录
    /** 今日新增术语数 */
    private Long termToday;          // 今日新增术语

    /** 月新增及累计增长趋势 */
    private List<TermGrowthRow> growth;   // 月新增 + 累计
    /** 产品维度启用率 */
    private List<ProductRatioRow> ratio;  // 产品维度启用率
    /** 创建人活跃度 TOP20 */
    private List<CreatorRankRow> creatorRank;      // 创建人活跃度 TOP20
    /** 角色权限矩阵 */
    private List<RolePermMatrixRow> rolePermMatrix;// 角色权限矩阵
    /** 桑基图节点 */
    private List<SankeyNode> sankeyNodes;          // 桑基图节点
    /** 桑基图边 */
    private List<SankeyLink> sankeyLinks;          // 桑基图边
    /** 项目未确认统计 */
    private List<ProjectPendingRow> projectPending;// 项目未确认统计


    /**
     * 术语增长行，表示某月新增及累计术语数量。
     */
    @Data
    @AllArgsConstructor
    public static class TermGrowthRow {
        /** 月份，格式 yyyy-MM */
        private String month;   // yyyy-MM
        /** 当月新增数量 */
        private Long newCount;
        /** 截至当月累计数量 */
        private Long totalCount;
    }
    /**
     * 产品维度启用率行。
     */
    @Data
    @AllArgsConstructor
    public static class ProductRatioRow {
        /** 产品名称，如 SmartOM / EMS / OnePoint */
        private String product; // SmartOM / EMS / OnePoint
        /** 已启用数量 */
        private Long enabled;
        /** 未启用数量 */
        private Long disabled;
    }
    /**
     * 创建人活跃度排行行。
     */
    @Data
    @AllArgsConstructor
    public static class CreatorRankRow {
        /** 用户名 */
        String username;
        /** 创建术语数量 */
        Long count;
    }
    /**
     * 角色权限矩阵行。
     */
    @Data
    @AllArgsConstructor
    public static class RolePermMatrixRow {
        /** 角色描述 */
        String role;
        /** 权限数量 */
        Long permCount;
    }
    /**
     * 项目未确认统计行。
     */
    @Data
    @AllArgsConstructor
    public static class ProjectPendingRow {
        /** 项目名称 */
        String project;
        /** 未确认数量 */
        Long pending;
    }
    /**
     * 桑基图节点。
     */
    @Data
    @AllArgsConstructor
    public static class SankeyNode {
        /** 节点名称 */
        String name;
        /** 术语数 */
        Long value;
    }   // value=术语数
    /**
     * 桑基图边。
     */
    @Data
    @AllArgsConstructor
    public static class SankeyLink {
        /** 源节点 */
        String source;
        /** 目标节点 */
        String target;
        /** 关联数量 */
        Long value;
    }
}
