-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id`     BIGINT       DEFAULT NULL COMMENT '操作人ID',
  `username`    VARCHAR(64)  DEFAULT '' COMMENT '操作人用户名',
  `operation`   VARCHAR(32)  DEFAULT '' COMMENT '操作类型：CREATE / UPDATE / DELETE',
  `module`      VARCHAR(32)  DEFAULT '' COMMENT '操作模块：term / directory',
  `description` VARCHAR(255) DEFAULT '' COMMENT '操作描述',
  `method`      VARCHAR(16)  DEFAULT '' COMMENT '请求方法：GET / POST / PUT / DELETE',
  `url`         VARCHAR(512) DEFAULT '' COMMENT '请求URL',
  `params`      TEXT         COMMENT '请求参数（JSON）',
  `result`      VARCHAR(16)  DEFAULT '' COMMENT '操作结果：SUCCESS / FAIL',
  `ip`          VARCHAR(64)  DEFAULT '' COMMENT '客户端IP',
  `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_module` (`module`),
  KEY `idx_operation` (`operation`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
