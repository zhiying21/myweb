# 枝莺前端 - Vue 3 + Vite 构建，Nginx 托管
FROM node:20-alpine AS builder

WORKDIR /app

# 复制依赖配置
COPY package.json package-lock.json* ./

# 安装依赖
RUN npm ci

# 复制源码并构建
COPY . .
RUN npm run build

# 运行阶段：Nginx 托管静态文件
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置（覆盖默认）
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
