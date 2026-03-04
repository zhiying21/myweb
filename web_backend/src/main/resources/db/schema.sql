-- 枝莺个人网站 - 数据库表结构

-- 示例表（兼容旧代码）
CREATE TABLE IF NOT EXISTS `demo` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(64)  DEFAULT NULL,
    `remark`      VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `email`       VARCHAR(128) NOT NULL COMMENT '邮箱',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`     VARCHAR(512)  DEFAULT NULL COMMENT '头像URL',
    `role`        VARCHAR(32)  DEFAULT 'USER' COMMENT '角色: USER, ADMIN',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 留言表
CREATE TABLE IF NOT EXISTS `message` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL COMMENT '留言用户ID',
    `content`     TEXT         NOT NULL COMMENT '留言内容',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 留言评论表（支持回复）
CREATE TABLE IF NOT EXISTS `message_comment` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `message_id`  BIGINT       NOT NULL COMMENT '留言ID',
    `user_id`     BIGINT       NOT NULL COMMENT '评论用户ID',
    `parent_id`   BIGINT       DEFAULT NULL COMMENT '父评论ID(回复用)',
    `reply_to_id` BIGINT       DEFAULT NULL COMMENT '回复的目标评论ID',
    `content`     TEXT         NOT NULL COMMENT '评论内容',
    `like_count`  INT          DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_message_id` (`message_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 评论点赞记录
CREATE TABLE IF NOT EXISTS `comment_like` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `comment_id`  BIGINT       NOT NULL,
    `user_id`     BIGINT       NOT NULL,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 资源分享表
CREATE TABLE IF NOT EXISTS `resource` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(128) NOT NULL COMMENT '软件/资源名称',
    `icon`        VARCHAR(512) DEFAULT NULL COMMENT '图标URL',
    `link`        VARCHAR(512) NOT NULL COMMENT '下载链接',
    `sort_order`  INT          DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 工单表（联系我）
CREATE TABLE IF NOT EXISTS `ticket` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       DEFAULT NULL COMMENT '提交用户ID(可选)',
    `email`       VARCHAR(128) NOT NULL COMMENT '联系邮箱',
    `subject`     VARCHAR(256) NOT NULL COMMENT '主题',
    `content`     TEXT         NOT NULL COMMENT '内容',
    `status`      VARCHAR(32)  DEFAULT 'PENDING' COMMENT '状态: PENDING, PROCESSING, RESOLVED',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 恋爱日记配置
CREATE TABLE IF NOT EXISTS `love_config` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `start_time`  DATETIME     NOT NULL COMMENT '恋爱开始时间',
    `name1`       VARCHAR(64)  DEFAULT NULL COMMENT '姓名1',
    `name2`       VARCHAR(64)  DEFAULT NULL COMMENT '姓名2',
    `avatar1`     VARCHAR(512) DEFAULT NULL COMMENT '头像1 URL',
    `avatar2`     VARCHAR(512) DEFAULT NULL COMMENT '头像2 URL',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 网站配置（运行开始时间、简历密码等）
CREATE TABLE IF NOT EXISTS `site_config` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `config_key`  VARCHAR(64)  NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(512) DEFAULT NULL COMMENT '配置值',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 笔记/博客文档表（上传的md）
CREATE TABLE IF NOT EXISTS `document` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `type`        VARCHAR(32)  NOT NULL COMMENT 'notes|blog',
    `title`       VARCHAR(256) NOT NULL,
    `description` VARCHAR(512) DEFAULT NULL,
    `content`     LONGTEXT     NOT NULL COMMENT 'Markdown内容',
    `cover_image` VARCHAR(512) DEFAULT NULL,
    `user_id`     BIGINT       DEFAULT NULL COMMENT '上传者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 简历内容表（可选，后续存储）
CREATE TABLE IF NOT EXISTS `resume` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `content`     LONGTEXT     NOT NULL COMMENT 'Markdown内容',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初始化网站配置（site_start_time 仅首次插入，resume_password 可更新）
INSERT INTO `site_config` (`config_key`, `config_value`) VALUES ('site_start_time', NOW())
ON DUPLICATE KEY UPDATE config_key = config_key;
INSERT INTO `site_config` (`config_key`, `config_value`) VALUES ('resume_password', 'AC_pH_under7')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);

-- 创建管理员：将已注册用户的 role 改为 ADMIN
-- UPDATE `user` SET `role` = 'ADMIN' WHERE `email` = 'your_admin@example.com';

-- 示例资源（可删除或修改，需在管理后台添加更多）
