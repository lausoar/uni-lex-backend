-- 术语版本变更日志表
CREATE TABLE IF NOT EXISTS `term_version_log` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `term_id`       BIGINT       NOT NULL COMMENT '术语ID',
  `dir_id`        BIGINT       DEFAULT NULL COMMENT '目录ID',
  `short_key`     VARCHAR(512) DEFAULT '' COMMENT '术语短键',
  `old_version`   VARCHAR(128) DEFAULT '' COMMENT '变更前版本',
  `new_version`   VARCHAR(128) DEFAULT '' COMMENT '变更后版本',
  `change_type`   VARCHAR(16)  DEFAULT 'UPDATE' COMMENT '变更类型：CREATE/UPDATE',
  `operator_id`   BIGINT       DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64)  DEFAULT '' COMMENT '操作人用户名',
  `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  PRIMARY KEY (`id`),
  KEY `idx_term_id` (`term_id`),
  KEY `idx_short_key` (`short_key`(191)),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='术语版本变更日志';
