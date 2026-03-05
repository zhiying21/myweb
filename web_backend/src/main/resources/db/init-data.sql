-- 枝莺个人网站 - 初始化数据
-- 执行本脚本前请先执行 schema.sql

USE `zhiying_web`;

-- 创建管理员账号（密码: admin123，BCrypt加密）
-- 可以注册后在数据库中手动将 role 改为 ADMIN
-- 此处直接插入一个加密好的密码版本
-- 密码原文: admin123456
INSERT INTO `user` (`email`, `password`, `nickname`, `avatar`, `role`)
VALUES ('admin@zhiying.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17ltrS', '管理员', 'https://api.dicebear.com/7.x/anime/svg?seed=admin', 'ADMIN')
ON DUPLICATE KEY UPDATE role = 'ADMIN';

-- 注：密码 admin123456 的 BCrypt 哈希为上面的值
-- 如需自定义密码，请使用 BCryptPasswordEncoder.encode("你的密码") 生成新的哈希值
-- 或注册后在数据库执行：
-- UPDATE user SET role = 'ADMIN' WHERE email = '你的邮箱';

-- 恋爱配置初始数据
INSERT INTO `love_config` (`start_time`, `name1`, `name2`, `avatar1`, `avatar2`)
SELECT '2024-01-01 00:00:00', '枝莺', 'TA', 
  'https://api.dicebear.com/7.x/anime/svg?seed=lover1',
  'https://api.dicebear.com/7.x/anime/svg?seed=lover2'
WHERE NOT EXISTS (SELECT 1 FROM `love_config` LIMIT 1);

-- 确保网站配置有初始数据（密码均为 20051021）
INSERT INTO `site_config` (`config_key`, `config_value`) VALUES ('site_start_time', DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'))
ON DUPLICATE KEY UPDATE config_key = config_key;

INSERT INTO `site_config` (`config_key`, `config_value`) VALUES ('resume_password', '20051021')
ON DUPLICATE KEY UPDATE config_key = config_key;

INSERT INTO `site_config` (`config_key`, `config_value`) VALUES ('love_diary_password', '20051021')
ON DUPLICATE KEY UPDATE config_key = config_key;

INSERT INTO `site_config` (`config_key`, `config_value`) VALUES ('visit_count', '0')
ON DUPLICATE KEY UPDATE config_key = config_key;

-- 初始简历内容
INSERT INTO `resume` (`content`)
SELECT '# 个人简历\n\n## 基本信息\n\n- **姓名**: 卢欢\n- **邮箱**: example@email.com\n- **GitHub**: github.com/zhiying\n\n## 教育背景\n\n### 某某大学（2021 - 2025）\n- 计算机科学与技术\n\n## 技术栈\n\n- **后端**: Java, Spring Boot, MyBatis-Plus, MySQL, Redis\n- **前端**: Vue.js, TypeScript, Vite\n- **工具**: Git, Docker, Maven\n\n## 项目经历\n\n### 个人网站（2024）\n\n使用 Spring Boot + Vue.js 开发的全栈个人网站，支持博客、留言、恋爱日记等功能。\n\n## 自我评价\n\n热爱编程，持续学习，追求代码质量。'
WHERE NOT EXISTS (SELECT 1 FROM `resume` LIMIT 1);
