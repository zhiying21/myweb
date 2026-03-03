# 枝莺个人网站 - 前端

Vue 3 + Vite + Vue Router + Axios，与后端 `web_backend` 前后端分离联调。

## 环境要求

- Node.js 18+
- npm

## 安装与运行

```bash
cd web_front
npm install
npm run dev
```

开发服务器：<http://localhost:5173>。  
请求会通过 Vite 代理转发到后端：`/api` → `http://localhost:8080/api`，需先启动后端。

## 脚本

| 命令 | 说明 |
|------|------|
| `npm run dev` | 开发模式 |
| `npm run build` | 生产构建（输出到 `dist/`） |
| `npm run preview` | 预览生产构建 |

## 目录结构

```
src/
├── api/           # 接口封装（按模块）
├── assets/        # 静态资源
├── components/    # 公共组件
├── router/        # 路由
├── utils/         # 工具（如 request）
├── views/         # 页面
├── App.vue
├── main.js
└── style.css
```

## 环境变量

- `.env.development`：开发环境，`VITE_API_BASE_URL=/api`（走代理）
- `.env.production`：生产环境，可按部署修改 `VITE_API_BASE_URL`

## 与后端联调

1. 启动后端：`cd web_backend && mvn spring-boot:run`（端口 8080）
2. 启动前端：`cd web_front && npm run dev`（端口 5173）
3. 浏览器访问 <http://localhost:5173>，首页会请求 `/api/health` 显示后端状态。
