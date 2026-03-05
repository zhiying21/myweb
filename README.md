# 枝莺个人网站 🌸

前后端分离的个人网站，后端 Spring Boot + MyBatis-Plus，前端 Vue 3 + Vite。

## 功能特性

- **首页**：网站运行时间（小时）、访问量、博客/笔记统计
- **登录/注册**：邮箱+密码，自动注册，JWT 鉴权，默认二次元头像
- **博客/笔记**：上传 Markdown 文档，点赞/浏览量统计，评论功能，作者/管理员可删除
- **留言板**：留言、评论、回复、点赞，显示头像和昵称，作者/管理员可删除
- **简历**：密码保护（默认 `20051021`），管理员在线编辑
- **恋爱日记**：密码保护（默认 `20051021`），日记/相册/纪念日 CRUD，实时恋爱时间
- **资源分享**：管理员上传资源（名称+图标+链接），可删除
- **联系我**：工单提交，管理员回复，用户在个人主页查看回复
- **个人主页**：修改昵称和头像，查看工单回复
- **管理后台**：工单管理、资源管理、简历编辑、恋爱日记配置

## 快速开始

### 环境要求

- Java 17+
- MySQL 8.0+
- Redis 6+
- Node.js 18+

### 数据库初始化

```sql
-- 1. 创建数据库
CREATE DATABASE zhiying_web DEFAULT CHARSET utf8mb4;

-- 2. 执行建表脚本
source web_backend/src/main/resources/db/schema.sql

-- 3. 执行初始数据脚本（创建管理员账号等）
source web_backend/src/main/resources/db/init-data.sql
```

### 管理员账号

初始管理员账号：
- 邮箱：`admin@zhiying.com`
- 密码：`admin123456`

或注册任意账号后，在数据库执行：
```sql
UPDATE user SET role = 'ADMIN' WHERE email = '你的邮箱';
```

### 后端启动

```bash
cd web_backend
# 修改 src/main/resources/application.yml 中的数据库和 Redis 配置
mvn spring-boot:run
```

后端端口：`8080`，API 前缀：`/api`

Swagger 文档：`http://localhost:8080/api/swagger-ui.html`

### 前端启动

```bash
cd web_front
npm install
npm run dev
```

前端端口：`5173`，代理 `/api` 到 `http://localhost:8080`

## 项目结构

```
hdb/
├── web_backend/          # Spring Boot 后端
│   ├── src/main/
│   │   ├── java/com/zhiying/
│   │   │   ├── config/       # 配置类（Security、Swagger、Redis等）
│   │   │   ├── controller/   # REST 控制器
│   │   │   ├── entity/       # 实体类
│   │   │   ├── mapper/       # MyBatis-Plus Mapper
│   │   │   ├── service/      # 业务逻辑
│   │   │   ├── security/     # JWT 过滤器
│   │   │   └── util/         # 工具类
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/           # SQL 脚本
└── web_front/            # Vue 3 前端
    ├── src/
    │   ├── api/          # API 封装模块
    │   ├── components/   # 公共组件
    │   ├── stores/       # 状态管理（auth）
    │   ├── utils/        # axios 封装
    │   └── views/        # 页面组件
    └── vite.config.js
```

## 密码默认值

| 功能 | 默认密码 |
|------|---------|
| 简历查看 | `20051021` |
| 恋爱日记 | `20051021` |
| 管理员账号 | `admin123456` |

> 密码可在数据库 `site_config` 表中修改（`config_key` 为 `resume_password` 或 `love_diary_password`）

## API 接口说明

| 模块 | 路径 | 说明 |
|------|------|------|
| 认证 | `/api/auth/**` | 登录、注册、用户信息 |
| 网站统计 | `/api/site/**` | 运行时间、访问量、统计数据 |
| 文档 | `/api/document/**` | 博客/笔记 CRUD、点赞、评论 |
| 留言板 | `/api/message/**` | 留言、评论、点赞 |
| 简历 | `/api/resume/**` | 简历内容读取 |
| 恋爱日记 | `/api/love/**` | 配置、日记、相册、纪念日 |
| 资源 | `/api/resource/**` | 资源列表 |
| 工单 | `/api/ticket/**` | 提交工单、查看工单 |
| 管理员 | `/api/admin/**` | 工单回复、资源管理、简历编辑 |
