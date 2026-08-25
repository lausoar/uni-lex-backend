-- =============================================================
-- 管理后台权限点种子脚本（"关闭公开注册 + 管理接口补鉴权"配套）
--
-- 背景：后端管理接口已加 @ReqPerm 注解，以下权限码必须存在于
--       sys_perm 并授予 ADMIN 角色，否则管理员会被锁在后台外。
--
-- 幂等：可重复执行，不会插入重复数据。
-- 注意：执行后需重启后端服务（权限查询带内存缓存，重启后生效）。
-- =============================================================

-- 1. 新增管理权限点
INSERT INTO sys_perm (perm_code, perm_name, is_system, created_at)
SELECT t.perm_code, t.perm_name, '1', NOW()
FROM (
    SELECT 'user:manage' AS perm_code, '用户管理'     AS perm_name
    UNION ALL SELECT 'role:manage', '角色管理'
    UNION ALL SELECT 'perm:manage', '权限点管理'
    UNION ALL SELECT 'role:perm',   '角色权限分配'
    UNION ALL SELECT 'user:role',   '用户角色分配'
    UNION ALL SELECT 'log:view',    '操作日志查看'
    UNION ALL SELECT 'dir:manage',  '目录管理'
) t
WHERE NOT EXISTS (
    SELECT 1 FROM sys_perm p WHERE p.perm_code = t.perm_code
);

-- 2. 将上述权限点授予 ADMIN 角色
--    （若库中 ADMIN 角色名不同，请先修改此处的角色名）
INSERT INTO sys_role_perm (role_id, perm_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_perm p ON p.perm_code IN (
    'user:manage', 'role:manage', 'perm:manage',
    'role:perm',   'user:role',   'log:view', 'dir:manage'
)
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_perm rp
      WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );
