## <font style="color:rgb(15, 17, 21);">Nginx 是什么？</font>
**<font style="color:rgb(15, 17, 21);">Nginx</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">是一个高性能的</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">Web服务器</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">和</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">反向代理服务器</font>**<font style="color:rgb(15, 17, 21);">。</font>

### <font style="color:rgb(15, 17, 21);">核心身份：</font>
+ <font style="color:rgb(15, 17, 21);">🚀</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">Web服务器</font>**<font style="color:rgb(15, 17, 21);">：像Apache一样，可以托管网站</font>
+ <font style="color:rgb(15, 17, 21);">🔄</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">反向代理</font>**<font style="color:rgb(15, 17, 21);">：作为客户端和后端服务器之间的中间层</font>
+ <font style="color:rgb(15, 17, 21);">⚖️</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">负载均衡器</font>**<font style="color:rgb(15, 17, 21);">：将流量分发到多个服务器</font>
+ <font style="color:rgb(15, 17, 21);">🔧</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">缓存服务器</font>**<font style="color:rgb(15, 17, 21);">：缓存内容提高访问速度</font>

---

## <font style="color:rgb(15, 17, 21);">Nginx 能做什么？</font>
### <font style="color:rgb(15, 17, 21);">1.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">静态资源服务</font>**
<font style="color:rgb(15, 17, 21);">nginx</font>

```plain
# 直接提供HTML、CSS、JS、图片等文件
location /static/ {
    alias /var/www/static/;
    expires 30d;  # 缓存30天
}
```

### <font style="color:rgb(15, 17, 21);">2.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">反向代理</font>**
<font style="color:rgb(15, 17, 21);">nginx</font>

```plain
# 将请求转发到后端应用服务器
location / {
    proxy_pass http://localhost:8000;  # 转发到Django应用
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

### <font style="color:rgb(15, 17, 21);">3.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">负载均衡</font>**
<font style="color:rgb(15, 17, 21);">nginx</font>

```plain
# 将流量分发到多个服务器
upstream app_servers {
    server 192.168.1.10:8000 weight=3;  # 权重3
    server 192.168.1.11:8000 weight=2;  # 权重2
    server 192.168.1.12:8000 backup;    # 备份服务器
}

location / {
    proxy_pass http://app_servers;
}
```

### <font style="color:rgb(15, 17, 21);">4.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">SSL终止</font>**
<font style="color:rgb(15, 17, 21);">nginx</font>

```plain
# HTTPS加密
server {
    listen 443 ssl;
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    # ...其他配置
}
```

### <font style="color:rgb(15, 17, 21);">5.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">URL重写和重定向</font>**
<font style="color:rgb(15, 17, 21);">nginx</font>

```plain
# 重写URL
rewrite ^/old-url$ /new-url permanent;

# 重定向
location /old-page {
    return 301 https://example.com/new-page;
}
```

---

## <font style="color:rgb(15, 17, 21);">Nginx 的实现原理</font>
### <font style="color:rgb(15, 17, 21);">事件驱动架构</font>
<font style="color:rgb(15, 17, 21);">bash</font>

```plain
# Nginx进程结构
主进程 (master process)
├── 工作进程 (worker process) 1
├── 工作进程 (worker process) 2
├── 工作进程 (worker process) 3
└── 工作进程 (worker process) 4
```

### <font style="color:rgb(15, 17, 21);">高性能秘诀：</font>
1. **<font style="color:rgb(15, 17, 21);">非阻塞I/O</font>**<font style="color:rgb(15, 17, 21);">：一个进程处理多个连接</font>
2. **<font style="color:rgb(15, 17, 21);">事件驱动</font>**<font style="color:rgb(15, 17, 21);">：基于事件通知，不轮询</font>
3. **<font style="color:rgb(15, 17, 21);">多进程</font>**<font style="color:rgb(15, 17, 21);">：充分利用多核CPU</font>
4. **<font style="color:rgb(15, 17, 21);">内存池</font>**<font style="color:rgb(15, 17, 21);">：减少内存分配开销</font>

---

## <font style="color:rgb(15, 17, 21);">Nginx vs Docker：根本区别</font>
### <font style="color:rgb(15, 17, 21);">Nginx</font>
<font style="color:rgb(15, 17, 21);">yaml</font>

```plain
类型: Web服务器/反向代理
功能: 处理HTTP请求、负载均衡、缓存
层级: 应用层 (Layer 7)
类比: 餐厅的"接待员"和"传菜员"
```

### <font style="color:rgb(15, 17, 21);">Docker</font>
<font style="color:rgb(15, 17, 21);">yaml</font>

```plain
类型: 容器化平台
功能: 应用打包、隔离、部署
层级: 操作系统层
类比: 餐厅的"厨房隔间"和"厨具标准化"
```

## <font style="color:rgb(15, 17, 21);">详细对比表</font>
| <font style="color:rgb(15, 17, 21);">特性</font> | <font style="color:rgb(15, 17, 21);">Nginx</font> | <font style="color:rgb(15, 17, 21);">Docker</font> |
| --- | --- | --- |
| **<font style="color:rgb(15, 17, 21);">本质</font>** | <font style="color:rgb(15, 17, 21);">Web服务器软件</font> | <font style="color:rgb(15, 17, 21);">容器化平台</font> |
| **<font style="color:rgb(15, 17, 21);">主要功能</font>** | <font style="color:rgb(15, 17, 21);">处理HTTP请求、代理、负载均衡</font> | <font style="color:rgb(15, 17, 21);">应用打包、隔离、部署</font> |
| **<font style="color:rgb(15, 17, 21);">工作层级</font>** | <font style="color:rgb(15, 17, 21);">应用层 (HTTP/HTTPS)</font> | <font style="color:rgb(15, 17, 21);">操作系统层 (进程隔离)</font> |
| **<font style="color:rgb(15, 17, 21);">依赖关系</font>** | <font style="color:rgb(15, 17, 21);">运行在操作系统上</font> | <font style="color:rgb(15, 17, 21);">管理应用程序的运行环境</font> |
| **<font style="color:rgb(15, 17, 21);">配置方式</font>** | <font style="color:rgb(15, 17, 21);">nginx.conf 配置文件</font> | <font style="color:rgb(15, 17, 21);">Dockerfile, docker-compose.yml</font> |
| **<font style="color:rgb(15, 17, 21);">扩展性</font>** | <font style="color:rgb(15, 17, 21);">水平扩展（多个Nginx实例）</font> | <font style="color:rgb(15, 17, 21);">容器副本、集群编排</font> |


---

## <font style="color:rgb(15, 17, 21);">实际应用场景</font>
### <font style="color:rgb(15, 17, 21);">场景1：传统Web应用</font>
<font style="color:rgb(15, 17, 21);">bash</font>

```plain
# Nginx作为Web服务器
用户请求 → Nginx → 静态文件
               ↓
           动态请求 → Django应用
```

### <font style="color:rgb(15, 17, 21);">场景2：微服务架构</font>
<font style="color:rgb(15, 17, 21);">bash</font>

```plain
# Nginx + Docker组合
用户请求 → Nginx (反向代理) → Docker容器1 (用户服务)
                         → Docker容器2 (订单服务)
                         → Docker容器3 (商品服务)
```

### <font style="color:rgb(15, 17, 21);">场景3：现代云原生</font>
<font style="color:rgb(15, 17, 21);">nginx</font>

```plain
# nginx.conf 配置示例
upstream auth_service {
    server auth-container-1:8080;
    server auth-container-2:8080;
}

upstream order_service {
    server order-container-1:8080;
}

server {
    listen 80;
    
    location /auth/ {
        proxy_pass http://auth_service;
    }
    
    location /orders/ {
        proxy_pass http://order_service;
    }
}
```

---

## <font style="color:rgb(15, 17, 21);">它们如何协同工作？</font>
### <font style="color:rgb(15, 17, 21);">典型架构：</font>
<font style="color:rgb(15, 17, 21);">text</font>

```plain
用户浏览器
     ↓
  Nginx (运行在宿主机或容器中)
     ↓
多个Docker容器 (运行不同的微服务)
     ↓
数据库、缓存等基础服务
```

### <font style="color:rgb(15, 17, 21);">docker-compose.yml 示例：</font>
<font style="color:rgb(15, 17, 21);">yaml</font>

```plain
version: '3'
services:
  nginx:
    image: nginx:latest
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - webapp
      - api
  
  webapp:
    image: my-webapp:latest
    ports:
      - "8000:8000"
  
  api:
    image: my-api:latest
    ports:
      - "3000:3000"
```

---

## <font style="color:rgb(15, 17, 21);">总结</font>
+ **<font style="color:rgb(15, 17, 21);">Nginx</font>**<font style="color:rgb(15, 17, 21);">：专注于"网络流量管理"</font>
+ **<font style="color:rgb(15, 17, 21);">Docker</font>**<font style="color:rgb(15, 17, 21);">：专注于"应用环境管理"</font>
+ **<font style="color:rgb(15, 17, 21);">关系</font>**<font style="color:rgb(15, 17, 21);">：不是二选一，而是经常配合使用</font>
+ **<font style="color:rgb(15, 17, 21);">类比</font>**<font style="color:rgb(15, 17, 21);">：Nginx像是交通警察，Docker像是标准化集装箱</font>

<font style="color:rgb(15, 17, 21);">在你的图书馆系统项目中：</font>

+ <font style="color:rgb(15, 17, 21);">可以用</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">Docker</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">来打包应用和环境</font>
+ <font style="color:rgb(15, 17, 21);">用</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">Nginx</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">作为反向代理提供Web服务</font>
+ <font style="color:rgb(15, 17, 21);">两者结合实现现代化部署</font>

