**<font style="color:rgb(15, 17, 21);">调度器</font>**<font style="color:rgb(15, 17, 21);">：</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">scrapy.core.scheduler.Scheduler</font>`<font style="color:rgb(15, 17, 21);"> + 队列 + 去重过滤器</font>

**<font style="color:rgb(15, 17, 21);">URL管理器</font>**<font style="color:rgb(15, 17, 21);">：调度器 + Spider 的 URL 生成逻辑</font>

**<font style="color:rgb(15, 17, 21);">下载器</font>**<font style="color:rgb(15, 17, 21);">：</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">scrapy.core.downloader.Downloader</font>`<font style="color:rgb(15, 17, 21);"> + 下载器中间件</font>

**<font style="color:rgb(15, 17, 21);">解析器</font>**<font style="color:rgb(15, 17, 21);">：Spider 的 </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">parse()</font>`<font style="color:rgb(15, 17, 21);"> 方法</font>

<font style="color:rgb(15, 17, 21);"></font>

**<font style="color:rgb(15, 17, 21);">Scrapy 的巧妙之处在于</font>**<font style="color:rgb(15, 17, 21);">：这些组件高度集成且可配置，你不需要手动管理它们，框架会自动协调工作！</font><!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2025/png/62181330/1763474262615-4a2aa8e1-91b2-424d-a54c-925ee271af9b.png)



scrapy startproject tutorial

在spider文件下创建quote.py

```plain
from pathlib import Path

import scrapy


class QuotesSpider(scrapy.Spider):
    name = "quotes"

    async def start(self):
        urls = [
            "https://quotes.toscrape.com/page/1/",
            "https://quotes.toscrape.com/page/2/",
        ]
        for url in urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        page = response.url.split("/")[-2]
        filename = f"quotes-{page}.html"
        Path(filename).write_bytes(response.body)
        self.log(f"Saved file {filename}")
```

<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">正如您所看到的，我们的 Spider 继承自</font>[<font style="color:rgb(41, 128, 185);background-color:rgb(252, 252, 252);">scrapy.Spider</font>](https://docs.scrapy.net.cn/en/latest/topics/spiders.html#scrapy.Spider)<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">，并定义了一些属性和方法</font>

+ [<font style="color:rgb(41, 128, 185);background-color:rgb(252, 252, 252);">name</font>](https://docs.scrapy.net.cn/en/latest/topics/spiders.html#scrapy.Spider.name)<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">：用于标识 Spider。它在项目内必须是唯一的，也就是说，您不能为不同的 Spider 设置相同的名称。</font>
+ [<font style="color:rgb(41, 128, 185);background-color:rgb(252, 252, 252);">start()</font>](https://docs.scrapy.net.cn/en/latest/topics/spiders.html#scrapy.Spider.start)<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">：必须是一个异步生成器，用于产生供 spider 开始抓取的请求（以及可选的 item）。后续请求将从这些初始请求中连续生成。</font>
+ [<font style="color:rgb(41, 128, 185);background-color:rgb(252, 252, 252);">parse()</font>](https://docs.scrapy.net.cn/en/latest/topics/spiders.html#scrapy.Spider.parse)<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">：一个将被调用的方法，用于处理每个请求下载的响应。response 参数是</font>[<font style="color:rgb(41, 128, 185);background-color:rgb(252, 252, 252);">TextResponse</font>](https://docs.scrapy.net.cn/en/latest/topics/request-response.html#scrapy.http.TextResponse)<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);"> </font><font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">的一个实例，它包含页面内容并具有其他有用的方法来处理它。</font>

[<font style="color:rgb(41, 128, 185);background-color:rgb(252, 252, 252);">parse()</font>](https://docs.scrapy.net.cn/en/latest/topics/spiders.html#scrapy.Spider.parse)<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);"> 方法通常会解析响应，将抓取到的数据提取为字典，并找到要跟踪的新 URL 并从中创建新的请求（</font>[<font style="color:rgb(41, 128, 185);background-color:rgb(252, 252, 252);">Request</font>](https://docs.scrapy.net.cn/en/latest/topics/request-response.html#scrapy.Request)<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">）。</font>

# ⭐<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);"> 示例：典型 Scrapy 模拟登录流程</font>
可以使用这两种方法维持会话,

1,cookies:最常见的方法, Scrapy 默认会为每个请求自动管理 cookies  

2.自定义的session搭配headers参数携带自定义的session信息

**具体流程**

+ **用户通过前端提交账号密码****：前端通过 POST 请求发送用户名和密码到 Flask 后端。**
+ **Flask 接收请求并启动 Scrapy****：Flask 后端接收用户的账号和密码，然后通过 Scrapy 启动爬虫，登录目标网站。**
+ **Scrapy 爬取信息****：爬虫登录网站后，根据需求抓取数据（例如，页面内容或用户信息）。**
+ **返回抓取结果****：抓取到的信息通过 Flask 返回给前端，前端再展示。**

```plain
from flask import Flask, request, jsonify
from scrapy.crawler import CrawlerProcess
from your_scrapy_project.spiders.your_spider import YourSpider

app = Flask(__name__)

@app.route('/login', methods=['POST'])
def login_and_scrape():
    # 获取前端传来的账号密码
    username = request.json.get('username')
    password = request.json.get('password')

    # 创建 Scrapy 爬虫进程
    process = CrawlerProcess()
    process.crawl(YourSpider, username=username, password=password)

    # 启动爬虫
    process.start()

    # 假设爬虫抓取到的数据保存在一个共享资源中
    result_data = "抓取的数据"

    # 返回数据给前端
    return jsonify({'data': result_data})

if __name__ == '__main__':
    app.run(debug=True)

```

```plain
import scrapy

class YourSpider(scrapy.Spider):
    name = "your_spider"
    
    def __init__(self, username, password, *args, **kwargs):
        super(YourSpider, self).__init__(*args, **kwargs)
        self.username = username
        self.password = password

    def start_requests(self):
        # 登录过程
        login_url = 'https://example.com/login'
        yield scrapy.FormRequest(url=login_url, formdata={
            'username': self.username,
            'password': self.password
        }, callback=self.after_login)

    def after_login(self, response):
        # 登录后继续爬取需要的数据
        yield scrapy.Request('https://example.com/data', callback=self.parse_data)

    def parse_data(self, response):
        # 解析数据
        data = response.xpath('//div[@class="info"]/text()').get()
        self.log(f'抓取的数据: {data}')
        # 将抓取的数据传回给 Flask
        yield {'data': data}

```

**启动 Flask 应用**：

+ <font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">启动 Flask 应用后，你可以通过前端发送 POST 请求，Flask 会启动 Scrapy 爬虫抓取数据，并将结果返回给前端：</font>

```plain
python app.py
```

<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);"></font>

<font style="color:rgb(64, 64, 64);background-color:rgb(252, 252, 252);">以一个常见网站登录为例：</font>

---

## **步骤 1：访问登录页获取 csrf token（如需要）**
```plain
class LoginSpider(scrapy.Spider):
    name = "login"

    def start_requests(self):
        login_page = "https://example.com/login"
        yield scrapy.Request(login_page, callback=self.parse_login)
```

---

## **步骤 2：解析登录页 token 并提交 POST 请求**
```plain
def parse_login(self, response):
    token = response.css("input[name=csrf_token]::attr(value)").get()

    data = {
        "username": "yourname",
        "password": "yourpassword",
        "csrf_token": token,
    }

    login_url = "https://example.com/do_login"
    yield scrapy.FormRequest(
        login_url,
        formdata=data,
        callback=self.after_login
    )
```

---

## **步骤 3：登录后访问需要权限的页面**
```plain
def after_login(self, response):
    if "登录成功" not in response.text:
        self.logger.error("登录失败！")
        return

    # Scrapy 会自动保持 cookies
    next_url = "https://example.com/user/profile"
    yield scrapy.Request(next_url, callback=self.parse_profile)
```

---

## **步骤 4：解析登录后页面**
```plain
def parse_profile(self, response):
    print("用户名：", response.css("h1::text").get())
```

# 🚀 **Scrapy 中 Cookie / Session 常用技巧**
---

# 1️⃣ 保存登录后 Cookie（调试很有用）
```plain
def after_login(self, response):
    cookies = response.headers.getlist('Set-Cookie')
    print("登录成功后的 cookies:", cookies)
```

---

# 2️⃣ 手动设置 Cookie
```plain
yield scrapy.Request(
    url,
    cookies={"token": "abcd"},
    callback=self.parse
)
```

---

# 3️⃣ 不想自动带 Cookie？关闭
```plain
yield scrapy.Request(
    url,
    dont_merge_cookies=True,
    callback=self.parse
)
```

---

# 4️⃣ Scrapy Shell 调试登录状态
非常强大：

```plain
scrapy shell https://example.com
```

你可以：

```plain
fetch("https://example.com/login")
response.css("title::text").get()
```

---

# 🔥 终极：使用 scrapy 的 **CookiesMiddleware** 自动保持登录（默认开启）
在 `settings.py`：

```plain
COOKIES_ENABLED = True
```

Scrapy 会自动：

+ 记录 Set-Cookie
+ 发送 Cookie
+ 保持会话（类似 requests.Session）

# scrapy命令行
格式:scrapy <command> [options] [args]

```plain
# 创建新Scrapy项目
scrapy startproject myproject
# 在项目中创建爬虫
scrapy genspider spider_name example.com
# 基本运行
scrapy crawl spider_name
# 带常用参数
scrapy crawl quotes -s LOG_LEVEL=INFO -s CONCURRENT_REQUESTS=16
# 输出到JSON文件
scrapy crawl quotes -o quotes.json

# 输出到CSV文件
scrapy crawl quotes -o quotes.csv

# 输出到JSON Lines格式
scrapy crawl quotes -o quotes.jl

# 不显示日志，只输出结果
scrapy crawl quotes --nolog

# 设置日志级别
scrapy crawl quotes -s LOG_LEVEL=WARNING
scrapy crawl quotes -s LOG_LEVEL=DEBUG
# 限制并发请求数
scrapy crawl quotes -s CONCURRENT_REQUESTS=8

# 设置下载延迟（秒）
scrapy crawl quotes -s DOWNLOAD_DELAY=1.5

# 限制请求深度
scrapy crawl quotes -s DEPTH_LIMIT=3

# 限制总请求数
scrapy crawl quotes -s CLOSESPIDER_PAGECOUNT=100
# 启用HTTP缓存
scrapy crawl quotes -s HTTPCACHE_ENABLED=True

# 强制刷新缓存
scrapy crawl quotes -s HTTPCACHE_ENABLED=True -s HTTPCACHE_FORCE_REFRESH=True

# 禁用去重过滤
scrapy crawl quotes -s DUPEFILTER_CLASS='scrapy.dupefilters.BaseDupeFilter'
# 查看项目中所有爬虫
scrapy list

# 查看爬虫详细信息
scrapy list -v
# 进入交互式Shell调试页面
scrapy shell "http://quotes.toscrape.com"

# 在Shell中测试选择器
>>> response.css('title::text').get()
>>> response.xpath('//title/text()').get()
# 快速测试爬虫（限制页面数）
scrapy crawl quotes -s CLOSESPIDER_PAGECOUNT=10

# 详细调试模式
scrapy crawl quotes -s LOG_LEVEL=DEBUG -s CONCURRENT_REQUESTS=1

```

