# 枝莺个人网站 - 功能实现说明

## 已实现功能

### 1. 简历
- **密码门**：打开前需输入密码 `AC_pH_under7`
- **内容来源**：`resource/卢欢的求职简历.md`（已复制到后端 `resources/resume/resume.md`）
- **界面**：白色简洁风格，分区块展示 Markdown 内容

### 2. 恋爱日记
- **密码门**：同简历密码 `AC_pH_under7`
- **布局**：中间两个动态头像，心跳线连接
- **恋爱时间**：实时显示天数、时、分、秒
- **管理后台**：管理员可编辑开始时间、姓名、头像 URL
- **UI**：浪漫紫色渐变背景

### 3. 笔记 & 博客
- **需登录**：游客需注册/登入后使用
- **上传**：支持上传本地 `.md` 文件或直接粘贴 Markdown
- **渲染**：自动 Markdown 渲染美化
- **数据**：存储到 `document` 表

### 4. 用户认证
- **注册/登录**：邮箱 + 密码
- **存储**：邮箱、密码（BCrypt 加密）
- **JWT**：7 天有效期
- **路由守卫**：笔记、博客、留言板、管理后台需登录

### 5. 首页
- **网站运行时间**：在「网站数据统计」区显示，单位：分钟
- **数据统计**：运行分钟、笔记数、博客数、访问量

### 6. 留言板
- **需登录**：仅登录用户可留言
- **功能**：留言、评论、回复评论、点赞
- **数据**：`message`、`message_comment`、`comment_like` 表

### 7. 资源分享
- **展示**：图标 + 软件名称，点击触发下载
- **管理**：管理员可在后台添加资源（名称、图标 URL、链接）

### 8. 联系我
- **工单**：提交主题、内容、邮箱
- **无需登录**：游客也可提交

### 9. 管理员后台
- **路径**：`/admin`
- **需登录**：且需 `ADMIN` 角色
- **功能**：查看工单、添加资源、配置恋爱日记

## 数据库

执行 `web_backend/src/main/resources/db/schema.sql` 创建表。

**创建管理员**：先注册一个用户，再执行：
```sql
UPDATE `user` SET `role` = 'ADMIN' WHERE `email` = '你的邮箱';
```

## 启动

1. 配置 MySQL、Redis（见 `application-dev.yml`）
2. 执行 schema.sql
3. 后端：`cd web_backend && mvn spring-boot:run`
4. 前端：`cd web_front && npm run dev`

## API 概览

| 路径 | 方法 | 说明 |
|------|------|------|
| /auth/register | POST | 注册 |
| /auth/login | POST | 登录 |
| /auth/me | GET | 当前用户（需 Token） |
| /site/running-minutes | GET | 网站运行分钟 |
| /site/resume/verify | POST | 验证简历密码 |
| /resume/content | GET | 简历内容 |
| /love/config | GET/POST | 恋爱配置（POST 需 ADMIN） |
| /resource/list | GET | 资源列表 |
| /document/list | GET | 文档列表（需登录） |
| /document/detail/:id | GET | 文档详情（需登录） |
| /document/upload | POST | 上传文档（需登录） |
| /message/* | - | 留言相关（需登录） |
| /ticket/create | POST | 提交工单 |
| /admin/tickets | GET | 工单列表（需 ADMIN） |
| /admin/resource | POST | 添加资源（需 ADMIN） |
