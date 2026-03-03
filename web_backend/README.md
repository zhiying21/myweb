# 枝莺个人网站 - 后端

Spring Boot 3 + MyBatis-Plus + MyBatis-Plus-Join + MySQL + Redis，前后端分离架构。

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8+
- Redis 6+

## 准备阶段

### 1. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS zhiying_web DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改配置

- `src/main/resources/application.yml` 或 `application-dev.yml` 中修改：
  - `spring.datasource.url / username / password`：MySQL 连接信息
  - `spring.data.redis.host / port / password`：Redis 连接信息

### 3. 启动

```bash
cd web_backend
mvn spring-boot:run
```

默认端口：`8080`，上下文：`/api`。健康检查：`GET http://localhost:8080/api/health`。

## 项目结构

```
com.zhiying
├── ZhiyingWebApplication.java    # 启动类
├── config/                        # 配置类
│   ├── MybatisPlusConfig.java     # MP 分页、自动填充
│   ├── RedisConfig.java           # Redis 序列化
│   └── CorsConfig.java            # 跨域
├── controller/                    # 控制层
├── service/                       # 业务层
├── mapper/                        # 数据访问（MP/MPJ）
├── entity/                        # 实体类
├── common/
│   ├── entity/                    # 基类实体
│   ├── result/                    # 统一响应、分页
│   ├── exception/                 # 异常与全局处理
│   └── util/                      # 工具类
└── (按业务模块可再分子包，如 user、article 等)
```

## 技术栈

- **Web**: Spring Boot Web、Validation
- **ORM**: MyBatis-Plus、MyBatis-Plus-Join（多表）
- **数据库**: MySQL、Lombok
- **缓存**: Spring Data Redis、Lettuce
- **规范**: 统一 Result 封装、全局异常、CORS
