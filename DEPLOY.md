# 枝莺个人网站 - Docker 部署指南

前后端分离架构，使用 Docker Compose 一键部署到服务器。

## 架构说明

| 服务 | 说明 | 端口 |
|------|------|------|
| frontend | Vue 3 前端 + Nginx | 80 |
| backend | Spring Boot 3 后端 | 8080（内部） |
| mysql | MySQL 8 数据库 | 3306 |
| redis | Redis 7 缓存 | 6379 |

- 用户访问 `http://服务器IP` → Nginx 提供前端静态文件
- 前端请求 `/api/*` → Nginx 代理到后端 `backend:8080`
- 后端连接 MySQL 和 Redis

## 一、服务器准备

### 1. 安装 Docker 和 Docker Compose

```bash
# 安装 Docker（以 Ubuntu 为例）
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
# 重新登录使 docker 组生效

# 安装 Docker Compose 插件
sudo apt install docker-compose-plugin

# 验证
docker --version
docker compose version
```

### 2. 上传项目到服务器

```bash
# 方式一：Git 克隆
git clone <你的仓库地址> zhiyingweb
cd zhiyingweb

# 方式二：本地打包上传
# 在本地执行：tar -czvf zhiyingweb.tar.gz --exclude=node_modules --exclude=target .
# 上传到服务器后：tar -xzvf zhiyingweb.tar.gz
```

## 二、配置环境变量

```bash
# 复制示例配置
cp .env.example .env

# 编辑 .env，填写 MySQL 密码（必填）
nano .env
```

`.env` 示例：

```env
DB_USER=root
DB_PASSWORD=你的强密码

# Redis 无密码可留空
REDIS_PASSWORD=
```

## 三、首次部署

### 1. 构建并启动

```bash
cd zhiyingweb
docker compose up -d --build
```

- `--build`：构建镜像（首次或代码更新后需要）
- `-d`：后台运行

### 2. 初始化数据库（如需要）

若后端有建表脚本，需在 MySQL 就绪后执行：

```bash
# 进入 MySQL 容器
docker compose exec mysql mysql -uroot -p你的密码 zhiying_web

# 或从本地 SQL 文件导入
docker compose exec -i mysql mysql -uroot -p你的密码 zhiying_web < init.sql
```

### 3. 查看运行状态

```bash
docker compose ps
docker compose logs -f backend   # 查看后端日志
docker compose logs -f frontend  # 查看前端日志
```

## 四、常用命令

| 操作 | 命令 |
|------|------|
| 启动 | `docker compose up -d` |
| 停止 | `docker compose down` |
| 重启 | `docker compose restart` |
| 重新构建并启动 | `docker compose up -d --build` |
| 查看日志 | `docker compose logs -f [服务名]` |
| 进入容器 | `docker compose exec backend sh` |

## 五、更新部署

代码更新后重新构建并启动：

```bash
git pull   # 若用 Git
docker compose up -d --build
```

## 六、访问与验证

- 前端：`http://服务器IP` 或 `http://你的域名`
- 后端健康检查：`http://服务器IP/api/health`

## 七、常见问题

### 1. 端口被占用

修改 `docker-compose.yml` 中的端口映射，例如：

```yaml
frontend:
  ports:
    - "8080:80"   # 改为 8080
```

### 2. 后端启动失败

- 检查 MySQL 是否就绪：`docker compose logs mysql`
- 检查 `.env` 中 `DB_PASSWORD` 是否正确
- 确认数据库 `zhiying_web` 已创建

### 3. 前端无法访问后端 API

- 确认 Nginx 配置中 `proxy_pass` 指向 `http://backend:8080`
- 检查后端是否正常：`docker compose exec backend wget -qO- http://localhost:8080/api/health`

### 4. 使用 HTTPS

建议在服务器前加一层 Nginx 或 Caddy 做反向代理和 SSL 终结，将 80/443 转发到本机的 80 端口。
