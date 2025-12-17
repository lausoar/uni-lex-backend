package com.unilex.backend.mapper;

import com.unilex.backend.vo.DashboardVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {

    /* 总览指标 */
    @Select("SELECT COUNT(*) FROM sys_user")
    Long userTotal();

    @Select("SELECT COUNT(*) FROM cat_directory WHERE dir_type=1")
    Long dirL1();
    @Select("SELECT COUNT(*) FROM cat_directory WHERE dir_type=2")
    Long dirL2();
    @Select("SELECT COUNT(*) FROM cat_directory WHERE dir_type=3")
    Long dirL3();

    @Select("SELECT COUNT(*) FROM term_entry")
    Long termTotal();
    @Select("SELECT COUNT(*) FROM term_entry WHERE confirmed=0")
    Long termPending();
    @Select("SELECT COUNT(DISTINCT project_name) " +
            "FROM term_entry " +
            "WHERE project_name IS NOT NULL AND project_name != ''")
    Long projectTotal();

    @Select("SELECT COUNT(*) FROM cat_directory WHERE DATE(created_at)=CURDATE()")
    Long dirToday();
    @Select("SELECT COUNT(*) FROM term_entry WHERE DATE(created_at)=CURDATE()")
    Long termToday();

    /* 月增长趋势 */
    @Select("SELECT DATE_FORMAT(created_at,'%Y-%m') month," +
            " COUNT(*) newCount," +
            " (SELECT COUNT(*) FROM term_entry t2 WHERE DATE_FORMAT(t2.created_at,'%Y-%m')<=month) totalCount" +
            " FROM term_entry" +
            " WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)" +
            " GROUP BY month" +
            " ORDER BY month")
    List<DashboardVo.TermGrowthRow> growth();

    /* 产品维度 */
    @Select("SELECT 'SmartOM' product," +
            " SUM(product_smartom) enabled," +
            " SUM(CASE WHEN product_smartom=0 THEN 1 ELSE 0 END) disabled FROM term_entry" +
            " UNION ALL " +
            "SELECT 'EMS' product," +
            " SUM(product_ems) enabled," +
            " SUM(CASE WHEN product_ems=0 THEN 1 ELSE 0 END) disabled FROM term_entry" +
            " UNION ALL " +
            "SELECT 'OnePoint' product," +
            " SUM(product_onepoint) enabled," +
            " SUM(CASE WHEN product_onepoint=0 THEN 1 ELSE 0 END) disabled FROM term_entry")
    List<DashboardVo.ProductRatioRow> ratio();

    /* 创建人活跃度：关联 sys_user 取 username，过滤 id=0，取前 20 */
    @Select("SELECT u.username, COUNT(*) cnt " +
            "FROM term_entry te " +
            "JOIN sys_user u ON te.creator = u.id " +
            "WHERE te.creator != 0 " +
            "GROUP BY u.username " +
            "ORDER BY cnt DESC " +
            "LIMIT 20")
    List<DashboardVo.CreatorRankRow> creatorRank();

    /*  角色权限矩阵 */
    @Select("SELECT r.desc role, COUNT(rp.perm_id) cnt FROM sys_role r LEFT JOIN sys_role_perm rp ON r.id=rp.role_id GROUP BY r.id")
    List<DashboardVo.RolePermMatrixRow> rolePermMatrix();

    /*  目录-术语桑基数据 */
    @Select("SELECT CONCAT(cd1.dir_name_en,'-',cd2.dir_name_en,'-',cd3.dir_name_en) path, COUNT(*) cnt " +
            "FROM cat_directory cd1 " +
            "JOIN cat_directory cd2 ON cd2.parent_id=cd1.id " +
            "JOIN cat_directory cd3 ON cd3.parent_id=cd2.id " +
            "JOIN term_entry te ON te.dir_id=cd3.id " +
            "GROUP BY path")
    List<Map<String, Object>> sankeyRaw();

    /*  项目未确认数 */
    @Select("SELECT project_name, COUNT(*) pending FROM term_entry " +
            "WHERE project_name IS NOT NULL AND confirmed=0 " +
            "GROUP BY project_name ORDER BY pending DESC")
    List<DashboardVo.ProjectPendingRow> projectPending();
}
