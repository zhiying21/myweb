-- 恋爱日记系统测试数据脚本
-- 用于快速测试系统的示例数据

-- 首先确保在正确的数据库上执行
USE zhiying_web;

-- 清理旧数据（可选，如果不想保留之前的数据）
-- DELETE FROM love_diary WHERE 1=1;
-- DELETE FROM love_reminder WHERE 1=1;
-- DELETE FROM love_photo WHERE 1=1;

-- ==================== 插入示例日记数据 ====================
INSERT INTO love_diary (title, content, date, emotion_score, emotion_analysis, is_public, create_time, update_time, deleted)
VALUES 
(
  '我们的开始',
  '# 美好的开始\n\n今天是一个特殊的日子，我们正式在一起了。\n\n你的笑容让我感到幸福和温暖，我会好好珍惜这段感情。\n\n❤️ 爱你',
  DATE_SUB(NOW(), INTERVAL 95 DAY),
  5,
  '💖 你的心里装满了幸福！继续珍惜彼此，一起走向更美好的未来。',
  0,
  DATE_SUB(NOW(), INTERVAL 95 DAY),
  DATE_SUB(NOW(), INTERVAL 95 DAY),
  0
),
(
  '第一次旅行',
  '# 一起去旅行\n\n## 今天的行程\n- 早上去了海边散步\n- 中午吃了当地美食\n- 下午在古镇里逛街\n- 晚上看了日落\n\n## 感受\n和你一起的每一刻都很珍贵，希望能有更多这样的回忆。\n\n最幸福的事就是和你在一起。',
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  5,
  '💖 你的心里装满了幸福！继续珍惜彼此，一起走向更美好的未来。',
  0,
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  0
),
(
  '平凡的美好',
  '# 简单却美好\n\n## 今天很平凡\n不是特殊的日子，但却有你，所以就很特殊。\n\n晚上一起看电影，你靠在我肩上，感觉这就是世界上最温暖的地方。\n\n生活就是这样，平淡中有最幸福的感受。',
  DATE_SUB(NOW(), INTERVAL 30 DAY),
  4,
  '😊 这段时光充满了温暖。多沟通，一起分享生活的小美好吧！',
  0,
  DATE_SUB(NOW(), INTERVAL 30 DAY),
  DATE_SUB(NOW(), INTERVAL 30 DAY),
  0
),
(
  '关于未来的谈话',
  '# 我们的未来\n\n## 今天的谈心\n我们聊了很多关于未来的事情，你的梦想激励着我。\n\n我想和你一起经历生活的每个阶段:\n- 一起努力工作\n- 有一个温暖的家\n- 去看遍世界的风景\n- 陪伴彼此到老\n\n和你在一起让我更加确定自己想要的生活。',
  DATE_SUB(NOW(), INTERVAL 10 DAY),
  5,
  '💖 你的心里装满了幸福！继续珍惜彼此，一起走向更美好的未来。',
  0,
  DATE_SUB(NOW(), INTERVAL 10 DAY),
  DATE_SUB(NOW(), INTERVAL 10 DAY),
  0
),
(
  '今天有点吵架',
  '## 有些小矛盾\n\n今天因为一些小事我们有点意见不合，但其实都不是什么大事。\n\n经过好好沟通后，我们都理解了对方的想法。\n\n这让我明白，在乎一个人有时候需要多一些耐心和理解。\n\n我们会变得更好的。',
  DATE_SUB(NOW(), INTERVAL 3 DAY),
  3,
  '😐 平淡即是福。记得问问对方的想法，加深彼此的了解。',
  0,
  DATE_SUB(NOW(), INTERVAL 3 DAY),
  DATE_SUB(NOW(), INTERVAL 3 DAY),
  0
),
(
  '又和好了',
  '## 雨后彩虹\n\n我们又和好了，你的道歉信让我哭了。\n\n你说："我们的感情值得我多一些耐心和包容。"\n\n这句话让我感受到你对这段关系的重视。\n\n我也会更努力去维护我们之间的感情。\n\n爱你❤️',
  DATE_SUB(NOW(), INTERVAL 2 DAY),
  5,
  '💖 你的心里装满了幸福！继续珍惜彼此，一起走向更美好的未来。',
  0,
  DATE_SUB(NOW(), INTERVAL 2 DAY),
  DATE_SUB(NOW(), INTERVAL 2 DAY),
  0
);

-- ==================== 插入示例纪念日数据 ====================
INSERT INTO love_reminder (title, date, description, frequency, is_enabled, create_time, update_time, deleted)
VALUES 
(
  '在一起 100 天',
  DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 100 DAY),
  '我们相识 100 天整，要继续一起走下去',
  'once',
  1,
  NOW(),
  NOW(),
  0
),
(
  '在一起 1 周年',
  DATE_ADD(DATE_SUB(NOW(), INTERVAL 95 DAY), INTERVAL 365 DAY),
  '纪念我们在一起的第一年',
  'yearly',
  1,
  NOW(),
  NOW(),
  0
),
(
  '你的生日',
  '2024-05-15 00:00:00',
  '最爱的人的生日，要给你一个惊喜',
  'yearly',
  1,
  NOW(),
  NOW(),
  0
),
(
  '我的生日',
  '2024-07-22 00:00:00',
  '我的生日，希望和你一起庆祝',
  'yearly',
  1,
  NOW(),
  NOW(),
  0
),
(
  '情人节',
  '2024-02-14 00:00:00',
  '一年一次的情人节，要好好陪你',
  'yearly',
  1,
  NOW(),
  NOW(),
  0
),
(
  '第1000天纪念',
  DATE_ADD(DATE_SUB(NOW(), INTERVAL 95 DAY), INTERVAL 1000 DAY),
  '我们在一起的第 1000 天，一个特殊的里程碑',
  'once',
  1,
  NOW(),
  NOW(),
  0
),
(
  '三周年纪念',
  DATE_ADD(DATE_SUB(NOW(), INTERVAL 95 DAY), INTERVAL 1100 DAY),
  '在一起三年了，又是一个重要回忆',
  'once',
  1,
  NOW(),
  NOW(),
  0
);

-- ==================== 插入示例照片数据 ====================
INSERT INTO love_photo (url, description, taken_date, tags, is_featured, create_time, update_time, deleted)
VALUES 
(
  '/uploads/love-photos/sample-1.jpg',
  '我们的第一张合照',
  DATE_SUB(NOW(), INTERVAL 95 DAY),
  '合照,开始,甜蜜',
  1,
  DATE_SUB(NOW(), INTERVAL 95 DAY),
  DATE_SUB(NOW(), INTERVAL 95 DAY),
  0
),
(
  '/uploads/love-photos/sample-2.jpg',
  '海边的日落',
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  '旅行,海边,日落,浪漫',
  1,
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  0
),
(
  '/uploads/love-photos/sample-3.jpg',
  '古镇散步',
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  '旅行,古镇,文化',
  0,
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  DATE_SUB(NOW(), INTERVAL 60 DAY),
  0
),
(
  '/uploads/love-photos/sample-4.jpg',
  '一起在咖啡厅',
  DATE_SUB(NOW(), INTERVAL 30 DAY),
  '日常,咖啡,温暖',
  1,
  DATE_SUB(NOW(), INTERVAL 30 DAY),
  DATE_SUB(NOW(), INTERVAL 30 DAY),
  0
),
(
  '/uploads/love-photos/sample-5.jpg',
  '樱花树下',
  DATE_SUB(NOW(), INTERVAL 15 DAY),
  '春天,樱花,浪漫,季节',
  1,
  DATE_SUB(NOW(), INTERVAL 15 DAY),
  DATE_SUB(NOW(), INTERVAL 15 DAY),
  0
),
(
  '/uploads/love-photos/sample-6.jpg',
  '家里的温暖时刻',
  NOW(),
  '家,温暖,平凡,幸福',
  1,
  NOW(),
  NOW(),
  0
);

-- ==================== 验证数据 ====================
-- 查看插入的数据
SELECT '=== 日记数据 ===' as info;
SELECT count(*) as total_diaries FROM love_diary WHERE deleted = 0;
SELECT id, title, date, emotion_score FROM love_diary WHERE deleted = 0 ORDER BY date DESC;

SELECT '=== 纪念日数据 ===' as info;
SELECT count(*) as total_reminders FROM love_reminder WHERE deleted = 0;
SELECT id, title, date, frequency, is_enabled FROM love_reminder WHERE deleted = 0;

SELECT '=== 照片数据 ===' as info;
SELECT count(*) as total_photos FROM love_photo WHERE deleted = 0;
SELECT id, description, taken_date, is_featured FROM love_photo WHERE deleted = 0;

-- ==================== 查询示例 ====================
-- 查询最近30天内的纪念日
-- SELECT * FROM love_reminder WHERE deleted = 0 AND date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 30 DAY);

-- 查询精选照片
-- SELECT * FROM love_photo WHERE deleted = 0 AND is_featured = 1;

-- 查询最高情感分数的日记
-- SELECT * FROM love_diary WHERE deleted = 0 ORDER BY emotion_score DESC LIMIT 5;

-- 统计数据
-- SELECT 
--   (SELECT COUNT(*) FROM love_diary WHERE deleted = 0) as diary_count,
--   (SELECT COUNT(*) FROM love_reminder WHERE deleted = 0) as reminder_count,
--   (SELECT COUNT(*) FROM love_photo WHERE deleted = 0) as photo_count;
