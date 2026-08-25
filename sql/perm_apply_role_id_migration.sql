-- =============================================================
-- perm_apply 表字段更名迁移：perm_id → role_id
--
-- 背景：权限申请单实际申请的是"角色"（前端下拉选角色、审批通过
--       后写 sys_user_role），但字段历史上误命名为 perm_id，
--       与 sys_perm 主键混用，导致审批可能授错权限。
--       后端实体已改为 roleId（映射 role_id 列），执行本脚本前
--       不要部署新版后端，否则会报未知列错误。
--
-- 注意：仅更名，数据完全保留。
-- =============================================================

-- perm_id 列的当前定义（已通过 SHOW COLUMNS 确认）：
--   bigint(20) | NOT NULL | 带普通索引(MUL)
-- CHANGE COLUMN 需要完整列定义；列上的索引在更名后会自动保留，无需重建。

-- MySQL 5.7 / 8.0 通用写法：
ALTER TABLE perm_apply CHANGE COLUMN perm_id role_id BIGINT(20) NOT NULL COMMENT '申请的角色ID';

-- MySQL 8.0+ 也可以用这个更简洁的写法（无需重复列定义，保留原有属性）：
-- ALTER TABLE perm_apply RENAME COLUMN perm_id TO role_id;
