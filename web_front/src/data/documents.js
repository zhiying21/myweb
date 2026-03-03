/**
 * 笔记和博客文档列表（来自 public 目录）
 * 新增文档时在此维护
 * image: 二次元风格占位图，可替换为本地或自定义图片
 * description: 简单描述
 */
const animeImage = (i) => `https://placewaifu.com/image/${200 + (i % 3)}/${200 + (i % 5)}`

export const notesList = [
  { path: '/笔记/Java!/Sping.md', title: 'Spring', category: 'Java!', description: 'Spring 框架核心概念与 IoC、AOP 依赖注入', image: animeImage(0) },
  { path: '/笔记/Java!/JUC.md', title: 'JUC', category: 'Java!', description: 'Java 并发工具包，线程安全与高并发编程', image: animeImage(1) },
  { path: '/笔记/Java!/JVM.md', title: 'JVM', category: 'Java!', description: 'JVM 内存模型、垃圾回收与类加载机制', image: animeImage(2) },
  { path: '/笔记/python爬虫/python基础库函数使用.md', title: 'Python基础库函数使用', category: 'python爬虫', description: 'Python 爬虫常用库与函数使用示例', image: animeImage(3) },
  { path: '/笔记/python爬虫/爬虫核心知识.md', title: '爬虫核心知识', category: 'python爬虫', description: '爬虫原理、反爬策略与数据解析', image: animeImage(4) },
  { path: '/笔记/python爬虫/scrapy.md', title: 'Scrapy', category: 'python爬虫', description: 'Scrapy 框架入门与实战爬虫开发', image: animeImage(5) },
  { path: '/笔记/Nginx笔记.md', title: 'Nginx笔记', category: '笔记', description: 'Nginx 配置、反向代理与负载均衡', image: animeImage(6) },
  { path: '/笔记/docker部署.md', title: 'Docker部署', category: '笔记', description: 'Docker 容器化部署与镜像管理', image: animeImage(7) },
  { path: '/笔记/Git笔记.md', title: 'Git笔记', category: '笔记', description: 'Git 版本控制与协作开发流程', image: animeImage(8) },
  { path: '/笔记/django笔记.md', title: 'Django笔记', category: '笔记', description: 'Django 框架与 Web 应用开发', image: animeImage(9) },
  { path: '/笔记/JAVA算法.md', title: 'JAVA算法', category: '笔记', description: '常用数据结构与算法实现', image: animeImage(10) },
  { path: '/笔记/docker常用参数.md', title: 'Docker常用参数', category: '笔记', description: 'Docker 命令常用参数速查', image: animeImage(11) },
  { path: '/笔记/C++ OpenCV.md', title: 'C++ OpenCV', category: '笔记', description: 'OpenCV 图像处理与计算机视觉', image: animeImage(12) },
  { path: '/笔记/消息队列.md', title: '消息队列', category: '笔记', description: '消息队列原理与异步解耦', image: animeImage(13) },
  { path: '/笔记/复合查询+内外链接.md', title: '复合查询+内外链接', category: '笔记', description: 'SQL 复合查询与内外连接', image: animeImage(14) },
  { path: '/笔记/Shell简介.md', title: 'Shell简介', category: '笔记', description: 'Shell 脚本入门与常用命令', image: animeImage(15) },
  { path: '/笔记/Linux简介.md', title: 'Linux简介', category: '笔记', description: 'Linux 系统基础与常用操作', image: animeImage(16) },
  { path: '/笔记/TS.md', title: 'TypeScript', category: '笔记', description: 'TypeScript 类型系统与进阶用法', image: animeImage(17) },
  { path: '/笔记/Qt考核笔记.md', title: 'Qt考核笔记', category: '笔记', description: 'Qt 跨平台 GUI 开发', image: animeImage(18) },
]

export const blogList = [
  { path: '/博客/内网穿透实现.md', title: '内网穿透实现', category: '博客', description: '内网穿透原理与 ngrok、frp 等工具使用', image: animeImage(19) },
  { path: '/博客/服务器租借使用.md', title: '服务器租借使用', category: '博客', description: '云服务器选购与基础配置指南', image: animeImage(20) },
]

/** 合并为统一文档列表 */
export const allDocuments = [
  ...notesList.map((d) => ({ ...d, type: 'notes' })),
  ...blogList.map((d) => ({ ...d, type: 'blog' })),
]
