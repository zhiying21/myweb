# docker常用参数

## **运行容器**

```
# 基本运行
docker run [OPTIONS] IMAGE [COMMAND]

# 常用参数
docker run -d                  # 后台运行（detached mode）
docker run -it                 # 交互模式+终端（interactive + TTY）
docker run --name mycontainer  # 指定容器名称
docker run --rm                # 退出后自动删除容器
docker run -p 80:80           # 端口映射 宿主机端口:容器端口,因为宿主机网络和容器内网络是隔离的
docker run -v /host:/container # 卷挂载
docker run -e VAR=value       # 设置环境变量
```

当镜像不存在时会自动拉取

## **查看容器**

```
docker ps                      # 运行中的容器
docker ps -a                   # 所有容器（包括停止的）
docker ps -l                   # 最近创建的容器
docker ps -q                   # 只显示容器ID
```

## **管理容器状态**

```
docker start <container>       # 启动容器
docker stop <container>        # 停止容器（优雅停止）
docker restart <container>     # 重启容器
docker pause <container>       # 暂停容器
docker unpause <container>     # 恢复容器
```

## **删除容器**

```
docker rm <container>          # 删除已停止的容器
docker rm -f <container>       # 强制删除运行中的容器
docker container prune         # 删除所有停止的容器
```

## **镜像操作**

```
docker images                  # 列出本地镜像
docker pull IMAGE[:TAG]        # 拉取镜像
docker push IMAGE[:TAG]        # 推送镜像
docker rmi IMAGE               # 删除镜像
docker image prune             # 删除未使用的镜像

# 镜像查找
docker search TERM             # 搜索 Docker Hub 镜像
```

## **构建镜像**

```
docker build -t name:tag .     # 构建镜像并打标签
docker build -f Dockerfile.dev . # 指定 Dockerfile
```

## **日志和信息**

```
docker logs <container>        # 查看容器日志
docker logs -f <container>     # 实时跟踪日志
docker logs --tail 100 <container> # 查看最后100行

docker inspect <container>     # 查看容器详细信息
docker stats                   # 实时资源统计
docker top <container>         # 查看容器内进程
```

## **网络管理**

```
docker network ls              # 列出网络
docker network create mynet    # 创建网络
docker network connect mynet container # 连接容器到网络

# 运行容器时指定网络
docker run --network mynet --name container1 nginx
```

## **卷和挂载**

```
docker volume ls               # 列出卷
docker volume create myvol     # 创建卷

# 挂载类型
docker run -v /host/path:/container/path    # 绑定挂载 主机路径:容器路径
docker run -v volume_name:/container/path   # 卷挂载
docker run --mount type=bind,source=/host/path,target=/container/path
```

## **常用工作流**

```
# 进入运行中的容器
docker exec -it <container> /bin/bash

# 复制文件
docker cp file.txt container:/path/
docker cp container:/path/file.txt ./

# 查看资源使用
docker system df              # Docker 磁盘使用
docker system prune           # 清理所有未使用的资源
```

## **一键清理**

```
# 清理所有未使用的镜像、容器、网络、构建缓存
docker system prune -a --volumes
```

## **实际应用示例**

```
# 开发环境典型用法
docker run -d --name dev-db -p 5432:5432 -e POSTGRES_PASSWORD=pass postgres:13

# 测试运行
docker run --rm -p 3000:3000 myapp:test

# 生产部署
docker run -d --name prod-app -p 80:80 --restart unless-stopped myapp:latest
```

# 解释

## 🌐 Nginx 是什么？

**Nginx** 是一个高性能的 **Web 服务器** 和 **反向代理服务器**。

### 主要用途：

- **静态网站服务** - 托管 HTML、CSS、JS 文件
- **反向代理** - 将请求转发给后端应用
- **负载均衡** - 分发流量到多个服务器
- **API 网关** - 管理微服务接口

## 💾 挂载卷（Volume）是什么？

**挂载卷** 是 Docker 中 **容器与主机之间共享文件** 的机制。

### 为什么需要挂载卷？

**问题：** 默认情况下，容器内的文件是"临时的"

- 容器删除 → 文件丢失
- 无法直接修改容器内文件
- 多个容器无法共享数据

**解决方案：** 使用挂载卷

------

## 📁 挂载卷的三种方式

### 1. **绑定挂载（Bind Mount）** - 最常用

将主机目录映射到容器目录

bash

```
# 语法：-v 主机路径:容器路径
docker run -d -p 80:80 -v /home/mywebsite:/usr/share/nginx/html nginx
```



**示例：**

bash

```
# 1. 创建本地网页文件
mkdir my-website
echo "<h1>我的自定义网站!</h1>" > my-website/index.html

# 2. 运行容器并挂载
docker run -d -p 8080:80 -v ${PWD}/my-website:/usr/share/nginx/html --name my-site nginx
```



现在访问 `http://localhost:8080` 看到的是你的自定义页面！

### 2. **命名卷（Named Volume）** - Docker 管理

bash

```
# Docker 自动创建和管理存储位置
docker volume create my-data
docker run -v my-data:/app/data my-app
```



### 3. **匿名卷（Anonymous Volume）**

bash

```
# Docker 自动创建，但难以管理
docker run -v /container/path nginx
```



------

## 🎯 实际应用示例

### 场景：开发网站

bash

```
# 你的项目结构：
# my-project/
#   ├── index.html
#   ├── css/
#   └── js/

# 运行开发环境
docker run -d -p 80:80 \
  -v ${PWD}/my-project:/usr/share/nginx/html \
  --name dev-site \
  nginx
```



**好处：**

- ✅ 在主机编辑文件，容器实时更新
- ✅ 容器删除后，文件还在主机上
- ✅ 便于版本控制（git）

------

## 🔄 对比：有挂载 vs 无挂载

### 无挂载（你刚才的情况）：

bash

```
docker run -d -p 80:80 nginx
```



- 网页文件在容器内部
- 修改困难
- 容器删除 → 文件丢失

### 有挂载（推荐做法）：

bash

```
docker run -d -p 80:80 -v ./my-site:/usr/share/nginx/html nginx
```



- 网页文件在主机上
- 方便编辑和版本控制
- 容器删除 → 文件保留

------

## 🛠️ 常用卷操作命令

bash

```
# 查看所有卷
docker volume ls

# 查看卷详情
docker volume inspect my-data

# 删除未使用的卷
docker volume prune
```



## 💡 总结

- **Nginx** = Web 服务器软件
- **挂载卷** = 容器与主机文件共享的桥梁
- **使用场景** = 需要持久化数据或开发时实时修改文件