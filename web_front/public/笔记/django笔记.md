题目

#### **项目名称：在线图书借阅管理系统**
**技术栈要求**：

+ 后端框架： **Python Django**
+ 数据库：**MySQL**
+ **ORM 框架**： **SQLAlchemy（Django**）

#### **核心需求**
1. *_用户与权限管理_
    - 实现管理员、普通用户两种角色（**基于 JWT 或 Session 的身份认证**）。
    - 管理员权限：图书信息 **CRUD**、用户管理（禁用 / 启用）、借阅记录审核。
    - 普通用户权限：查询图书、借阅 / 归还图书、查看个人借阅记录。
2. **图书与借阅业务**
    - 图书信息包含：**ISBN**、书名、作者、分类、库存数量、出版社、入库时间。
    - 借阅规则：单用户最多借阅 5 本，最长借阅 30 天，超期需计算罚款（0.5 元 / 天）。
    - 实现借阅流程：用户申请→系统检查库存与用户状态→生成借阅记录（**含预计归还时间**）；归还时更新状态并计算超期罚款。
3. **数据查询与统计**
    - 支持图书多条件筛选（分类、作者、书名模糊查询）。
    - 管理员端统计功能：近 30 天借阅量 TOP10 图书、用户借阅活跃度排行、分类借阅占比。
4. **性能与扩展性**
    - 针对高频查询（如热门图书）实现简单缓存（如 Redis，可选）。
    - 设计数据库索引优化查询性能（需提交索引设计说明）。

#### **考核要点**：
+ 数据库表结构设计（至少包含用户表、图书表、借阅记录表，需考虑关联关系与冗余字段）。
+ 业务逻辑完整性（如借阅冲突处理、超期计算的准确性）。
+ 代码规范性（异常处理、注释、**分层架构：Controller/Service/DAO**）。

# bit考核技术栈
## 1.Django
**Django** 是一个基于 Python 的高级 Web 框架，它遵循 **"MTV"**（Model-Template-View）模式，让开发者能够快速、简洁地构建高质量的 Web 应用程序

```plain
py -m pip install Django==5.2.7
```

cmd输入下载

## 核心概念
### Django 的架构模式：MTV
python

```plain
# Model（模型） - 数据处理层
class User(models.Model):
    name = models.CharField(max_length=100)
    email = models.EmailField()

# Template（模板） - 表现层
# user_list.html
{% for user in users %}
    <li>{{ user.name }} - {{ user.email }}</li>
{% endfor %}

# View（视图） - 业务逻辑层
def user_list(request):
    users = User.objects.all()
    return render(request, 'user_list.html', {'users': users})
```



---

## Django 的主要特点
### 1. **"开箱即用"理念**
Django 自带了很多常用功能：

python

```plain
# 内置功能包括：
✅ 用户认证系统
✅ 管理员后台
✅ 表单处理
✅ 数据库ORM
✅ URL路由
✅ 模板引擎
✅ 缓存系统
✅ 国际化支持
```



### 2. **强大的 ORM（对象关系映射）**
python

```plain
# 不用写SQL，用Python操作数据库
from myapp.models import Article

# 查询所有文章
articles = Article.objects.all()

# 条件查询
recent_articles = Article.objects.filter(
    pub_date__gte=timezone.now() - timedelta(days=7)
)

# 创建新文章
new_article = Article(title="Hello Django", content="...")
new_article.save()
```



### 3. **自动生成管理后台**
python

```plain
# admin.py
from django.contrib import admin
from .models import Article

@admin.register(Article)
class ArticleAdmin(admin.ModelAdmin):
    list_display = ['title', 'pub_date', 'author']
    search_fields = ['title', 'content']
```



访问 `/admin` 就能看到功能完善的后台管理系统。

---

## Django 项目结构
### 典型项目布局：
text

```plain
myproject/                 # 项目根目录
├── manage.py             # 项目管理脚本
├── myproject/            # 项目配置目录
│   ├── __init__.py
│   ├── settings.py       # 项目设置
│   ├── urls.py           # 项目URL配置
│   └── wsgi.py           # WSGI入口
└── myapp/                # 应用目录  一个项目中会有多个startapp
    ├── migrations/       # 数据库迁移文件
    ├── __init__.py
    ├── admin.py          # 管理员配置
    ├── apps.py           # 应用配置
    ├── models.py         # 数据模型
    ├── tests.py          # 测试用例
    ├── views.py          # 视图函数
    └── templates/        # 模板文件
```







# django 代码,函数,基础操作
### 1.  路由urls.py
urls.py里面的文件就像一个接线员,接受来自HTTP的请求,然后通过路由规则(URL模式),用"分机表"把电话转接到不同的部门

```python
# my_site/urls.py (项目根目录下的urls.py)
from django.contrib import admin
from django.urls import path, include  # 注意这里导入了 include

urlpatterns = [
    path('admin/', admin.site.urls),  # 管理后台的路由
    path('blog/', include('blog.urls')),  # 包含应用的路由
    path('', include('home.urls')),  # 包含首页应用的路由（空路径）
]
path('admin/', admin.site.urls)：当用户访问 http://你的网站/admin/ 时，Django 会将请求交给自带的 admin 管理后台处理。

path('blog/', include('blog.urls'))：当用户访问 http://你的网站/blog/... 时，Django 会去 blog 应用下的 urls.py 文件中寻找更详细的路由配置。这叫 “路由分发”。

path('', ...)：空字符串代表根路径（即 http://你的网站/）。
```

python manage.py runserver 启动运行

#### **路由的两种主要类型**
1. **直接路由**（如示例中的 admin 路由）

```python
path('about/', views.about_page),  # 直接指向一个视图函数
```

2. **分发路由**（模块化设计）

```python
path('blog/', include('blog.urls')),  # 转发到blog应用的路由文件
path('shop/', include('shop.urls')),  # 转发到shop应用的路由文件
这将跳转到另一个app文件
```

**普通路径匹配**

```plain
path('admin/', admin.site.urls)
path()：普通路径匹配函数
'admin/'：匹配以 admin/ 开头的 URL
admin.site.urls：指向 Django 自带的后台管理页面
访问示例：http://127.0.0.1:8000/admin/
```

**正则表达式路径匹配**

```plain
re_path("^index/([0-9]{4})/$", views.index)
re_path()：正则表达式路径匹配函数
"^index/([0-9]{4})/$"：正则表达式模式
views.index：指向 views.py 中的 index 视图函数
如:/index/2023/    将匹配成功 
缺点是不简洁易读,不推荐使用
```

#### 反向解析
随着功能的增加，路由层的 url 发生变化，就需要去更改对应的视图层和模板层的 url，非常麻烦，不便维护。这时我们可以利用反向解析，当路由层 url 发生改变，在视图层和模板层动态反向解析出更改后的 url，免去修改的操作。

反向解析一般用在模板中的超链接及视图中的重定向。

在 urls.py 中给路由起别名，**name="路由别名"**。

```plain
path("login1/", views.login, name="login")
```

在 views.py 中，从 django.urls 中引入 reverse，利用 **reverse("路由别名")** 反向解析:

```plain
return redirect(reverse("login"))
```

在模板 templates 中的 HTML 文件中，利用 **{% url "路由别名" %}** 反向解析。

```plain
<form action="{% url 'login' %}" method="post"> 
```

在view.py中使用app中的路由

```plain
return redirect(reverse("app01:login")
```







### 2.setting.py
```plain
DEBUG=Ture/False #在部署前一定要先改成False模式,否则别人也能看到数据库,路径等等敏感信息
```

#### **1. ALLOWED_HOSTS - 安全防护**
```plain
ALLOWED_HOSTS = []
```

+ **作用**：这是一个**安全白名单**，告诉 Django 只接受来自这些域名/IP 的请求。
+ **工作方式**：
    - `DEBUG = True` 时：可以为空，Django 只接受 `localhost` 和 `127.0.0.1`
    - `DEBUG = False` 时：**必须配置**，否则服务器会拒绝所有外部请求**示例**

```plain
# 生产环境配置
ALLOWED_HOSTS = ['www.mysite.com', 'mysite.com', '123.45.67.89']

# 开发环境（接受所有主机，仅用于测试）
ALLOWED_HOSTS = ['*']
```

#### **2.INSTALLED_APPS - 应用注册表**
```plain
INSTALLED_APPS = [
    'django.contrib.admin',      # 🎯 管理员后台系统
    'django.contrib.auth',       # 🔐 用户认证系统（登录/注册/权限）
    'django.contrib.contenttypes', # 📦 内容类型框架
    'django.contrib.sessions',   # 💺 会话管理（保持用户登录状态）
    'django.contrib.messages',   # 💬 消息框架（操作成功/错误提示）
    'django.contrib.staticfiles', # 📁 静态文件管理（CSS/JS/图片）
]
```

**为什么需要注册应用？**

+ 就像手机需要安装 App 才能使用功能一样，Django 需要知道哪些应用应该被激活。
+ 每个注册的应用可以：
    - 创建数据库表
    - 提供管理界面
    - 添加 URL 路由
    - 注册模板和静态文件

**添加自定义应用**：

python

```plain
INSTALLED_APPS = [
    ...  # 默认应用
    'blog',          # 你的博客应用
    'users',         # 用户管理应用
    'api',           # API 接口应用
]
```

#### **3. DATABASES - 数据库配置**
```plain
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',  # 🗃️ 数据库引擎
        'NAME': BASE_DIR / 'db.sqlite3',         # 📄 数据库文件路径
    }
}这是django的默认数据库SQLite
```

```plain
# MySQL 配置示例
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'mydatabase',
        'USER': 'mydatabaseuser',
        'PASSWORD': 'mypassword',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}可以自行配置其他数据库
```

#### **4. STATIC_URL - 静态文件配置**
```plain
STATIC_URL = 'static/'
定义静态文件（CSS, JavaScript, 图片）的访问 URL 前缀。
```

+ **示例**：
    - 如果有一个文件 `style.css`，可以通过 `http://localhost:8000/static/style.css` 访问
+ **相关配置**：python

```plain
# 静态文件收集目录（生产环境）
STATIC_ROOT = BASE_DIR / 'staticfiles'

# 额外的静态文件目录
STATICFILES_DIRS = [
    BASE_DIR / 'static',
]
```

### **3. manage.py（项目管理脚本）**
**示例:**

```plain
#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "HelloWorld.settings")
    from django.core.management import execute_from_command_line
    execute_from_command_line(sys.argv)
```

#!/usr/bin/env python

+ **作用**：在 Unix/Linux 系统中，告诉系统使用哪个解释器来执行这个脚本。
+ **含义**：使用环境变量中的 `python` 命令来运行此文件。
+ **在 Windows 上**：这行被忽略，不影响使用。

**第2-3行：导入模块**

```plain
import os
import sys
```

+ `os`：提供与操作系统交互的功能（如环境变量、文件路径）
+ `sys`：提供与 Python 解释器交互的功能（如命令行参数）

if **name** == "**main**":主程序入口

```plain
s.environ.setdefault("DJANGO_SETTINGS_MODULE", "HelloWorld.settings")
```

+ **关键作用**：告诉 Django**使用哪个项目的配置文件**。
+ `DJANGO_SETTINGS_MODULE`：Django 寻找配置文件的环境变量。
+ `"HelloWorld.settings"`：指定使用 `HelloWorld` 项目下的 `settings.py` 文件。
+ `setdefault()`：如果环境变量已存在，就不修改；如果不存在，就设置为指定值。

```plain
from django.core.management import execute_from_command_line
execute_from_command_line(sys.argv)
```



+ **第7行**：导入 Django 的命令行执行器。
+ **第8行**：**核心功能** - 解析并执行命令行命令。
+ `sys.argv`：获取用户在命令行中输入的所有参数。

---

**工作原理示例**

当您在命令行中输入：

bash

```plain
python manage.py runserver
```

1. `sys.argv` 得到：`['manage.py', 'runserver']`
2. `execute_from_command_line()` 解析这个列表
3. 识别出 `runserver` 命令
4. 调用 Django 的启动服务器功能
5. 在 `http://127.0.0.1:8000` 启动开发服务器

```plain
其中七八行一般会写成更安全的显示
try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)
```

### 4.views.py
+ 每个 Django 视图函数都至少接收一个 `request` 参数，包含 HTTP 请求信息

```plain
'''
from django.http import HttpResponse
 
def hello(request):
    return HttpResponse("Hello world ! ")
'''
from django.shortcuts import render
 
def runoob(request):
    context          = {}   #创建一个空的字典 context
    context['hello'] = 'Hello World!'   #向字典中添加键值对：'hello' 对应 'Hello World!'
    return render(request, 'runoob.html', context) 
    #request: HTTP 请求对象
    #runoob.html: 要渲染的模板文件
    #context: 包含要传递给模板的数据

```

**request属性**

```plain
request.GET:数据类型是 QueryDict，一个类似于字典的对象，包含 HTTP GET 的所有参数。
有相同的键，就把所有的值放到对应的列表里。
request.POST:数据类型是 QueryDict，一个类似于字典的对象，包含 HTTP POST 的所有参数。
常用于 form 表单，form 表单里的标签 name 属性对应参数的键，value 属性对应参数的值。
request.get()：返回字符串，如果该键对应有多个值，取出该键的最后一个值。
request.path:获取 URL 中的路径部分，数据类型是字符串。
request.method:获取当前请求的方式，数据类型是字符串，且结果为大写。

```

**响应对象：HttpResponse 对象**

```plain
HttpResponse(): 返回文本，参数为字符串，字符串中写文本内容。如果参数为字符串里含有 html 标签，也可以渲染。
def runoob(request):
    # return HttpResponse("菜鸟教程")
    return HttpResponse("<a href='https://www.runoob.com/'>菜鸟教程</a>")
    
render(): 返回文本，第一个参数为 request，第二个参数为字符串（页面名称），第三个参数为字典（可选参数，向页面传递的参数：键为页面参数名，值为views参数名）。
def runoob(request):
    name ="菜鸟教程"
    return render(request,"runoob.html",{"name":name})
    
redirect()：重定向，跳转新页面。参数为字符串，字符串中填写页面路径。一般用于 form 表单提交后，跳转到新页面。
def runoob(request):
    return redirect("/index/")
```



**views.py 的常见职责**

#### 1. **数据查询和过滤**
python

```plain
# 复杂查询
books = Book.objects.filter(
    category='文学',
    stock_quantity__gt=0
).order_by('-publish_date')[:10]
```

#### 2. **表单处理**
python

```plain
from django.shortcuts import redirect

def create_book(request):
    if request.method == 'POST':
        form = BookForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect('book_list')  # 重定向
    else:
        form = BookForm()
    return render(request, 'create_book.html', {'form': form})
```

#### 3. **用户认证**
python

```plain
from django.contrib.auth.decorators import login_required

@login_required
def my_books(request):
    # 只有登录用户能访问
    books = Book.objects.filter(borrower=request.user)
    return render(request, 'my_books.html', {'books': books})
```

#### 4. **API 响应**
python

```plain
from django.http import JsonResponse

def book_api(request):
    books = Book.objects.all().values('id', 'title', 'author')
    return JsonResponse(list(books), safe=False)
```

#### 总结
**views.py 的作用**：

+ ✅ **请求处理器** - 接收和响应HTTP请求
+ ✅ **业务逻辑中心** - 实现具体功能
+ ✅ **数据协调器** - 连接模型和模板
+ ✅ **流程控制器** - 控制页面跳转和逻辑流程

简单说：**views.py 是 Django 应用的大脑，负责决定"做什么"和"怎么显示"**。

### 5.static
配置静态文件

1、在项目根目录下创建 static 目录。

2、在 settings 文件的最下方配置添加以下配置：

```plain
STATIC_URL = '/static/' # 别名 
STATICFILES_DIRS = [ 
    os.path.join(BASE_DIR, "static"), 
]
```

3、在 static 目录下创建 css 目录，js 目录，images 目录，plugins 目录， 分别放 css文件，js文件，图片，插件。

4、把 bootstrap 框架放入插件目录 plugins。

5、在 HTML 文件的 head 标签中引入 bootstrap。

templates就是用于将业务逻辑（Python）与展示层（HTML）分离的核心组件，它允许开发者通过简单的标签和变量动态生成 HTML 页面。templates/     和static/   就是django的模型

### 6.app
#### app与django模型的关系
+ **模型必须属于某个应用**
+ **应用必须在 INSTALLED_APPS 中注册**
+ **一个应用可以包含多个模型**
+ **不同应用的模型可以相互关联**
+ 这种设计支持 Django 的"可插拔应用"理念

简单说：**App 是功能模块的容器，Model 是模块中的数据定义**。

当我们使用apps/下的models.py时,需要在 settings.py 中找到INSTALLED_APPS这一项，如下：

```plain
INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'apps',               # 添加此项
)
```

#### models.py
**示例:**

```plain
from django.db import models

class Test(models.Model):
    name = models.CharField(max_length=20)
```

在命令行中

```plain
$ python3 manage.py migrate   # 创建表结构
$ python3 manage.py makemigrations apps  # 让 Django 知道我们在我们的模型有一些变更
$ python3 manage.py migrate apps   # 创建表结构
```

看到几行 "Creating table…" 的字样，你的数据表就创建好了。table名字是apps_Test

### 7.数据库操作
可以在urls.py同目录下创建一个testdb.py文件,在urls.py中加上path

```plain
 path('testdb/', testdb.testdb)
```

然后在testdb.py中

```plain
-*- coding: utf-8 -*-
from django.http import HttpResponse
from TestModel.models import Test
def testdb(request):
    test1 = Test(name='runoob')
    test1.save()   #这里的.save()相当于SQL中的INSERT
    return HttpResponse("<p>数据添加成功！</p>")
```

当文件中包含,中文,特殊符号,表情符号,日文,韩文,俄文时需要在文件开头加上

```python
# -*- coding: utf-8 -*-
```

**Django提供了多种方式来获取数据库的内容，如下代码所示：**

```python
def testdb(request):
    # 初始化
    response = ""
    response1 = ""
    
    # 通过objects这个模型管理器的all()获得所有数据行，相当于SQL中的SELECT * FROM
    listTest = Test.objects.all()
        
    # filter相当于SQL中的WHERE，可设置条件过滤结果
    response2 = Test.objects.filter(id=1) 
    
    # 获取单个对象
    response3 = Test.objects.get(id=1) 
    
    # 限制返回的数据 相当于 SQL 中的 OFFSET 0 LIMIT 2;
    Test.objects.order_by('name')[0:2]
    
    #数据排序
    Test.objects.order_by("id")
    
    # 上面的方法可以连锁使用
    Test.objects.filter(name="runoob").order_by("id")
    
    # 输出所有数据
    for var in listTest:
        response1 += var.name + " "
    response = response1
    return HttpResponse("<p>" + response + "</p>")

    #更新数据
    test1 = Test.objects.get(id=1)
    test1.name = 'Google'
    test1.save()
    
    #删除数据
    test1 = Test.objects.get(id=1)
    test1.delete()
```





### 8.django-admin/python manager.py
django-admin help 可以查看所有函数

#### **1.django-admin startproject 项目名称**
+ `manage.py`：项目管理脚本项目名称/：项目主目录
    - `__init__.py`
    - `settings.py`：项目设置文件
    - `urls.py`：URL 路由配置
    - `wsgi.py`：WSGI 应用入口

#### **2.django-admin startapp <应用名称> [目标目录]**
```plain
这会创建一个新的 Django 应用，包含：

migrations/：数据库迁移文件目录
__init__.py
admin.py：管理后台配置
apps.py：应用配置
models.py：数据模型定义
tests.py：测试代码
views.py：视图函数
```

**3.django-admin check**检查项目配置(URL,Model,staticfiles)

#### **4. 数据库迁移**
Django 使用迁移系统来管理数据库模式变更：

```plain
django-admin makemigrations  # 创建迁移文件
django-admin migrate        # 应用迁移到数据库
```

#### **5.创建超级用户**
```plain
django-admin createsuperuser
```

#### **6.启动开发服务器**
```plain
python manage.py runserver
```

**数据库迁移原理及迁移的实现**

**1. 创建迁移（makemigrations）**

当你运行 `python manage.py makemigrations` 时：

1. **扫描模型**：Django 检查所有已注册应用中的 `models.py` 文件
2. **对比差异**：将当前模型与之前的迁移记录进行比较
3. **生成操作**：创建一系列数据库操作（创建表、添加字段、修改约束等）
4. **生成文件**：在应用的 `migrations/` 目录下生成新的迁移文件

**2. 应用迁移（migrate）**

当你运行 `python manage.py migrate` 时：

1. **检查 django_migrations 表**：Django 在数据库中有一个特殊的表，记录已应用的迁移
2. **找出未应用的迁移**：对比迁移文件和应用记录，找到需要执行的新迁移
3. **执行 SQL**：将迁移中的操作转换为具体数据库的 SQL 语句并执行
4. **更新记录**：在 django_migrations 表中记录已应用的迁移



urls.py连接view.py 

```plain
import . from views
```



新建templates,可以在里面存放html文件,然后在setting.py文件的templates文件里

```plain
'DIRS': [BASE_DIR / "templates"],       # 修改位置
```



+ 导入 Django 的 `render` 函数，用于渲染模板并返回 HTTP 响应

```plain
from django.shortcuts import render
```

**过滤器**

模板语法：

```plain
{{ 变量名 | 过滤器：可选参数 }}
```

模板过滤器可以在变量被显示前修改它，过滤器使用管道字符，如下所示：

```plain
{{ name|lower }}
```

{{ name }} 变量被过滤器 lower 处理后，文档大写转换文本为小写。

过滤管道可以被* 套接* ，既是说，一个过滤器管道的输出又可以作为下一个管道的输入：

```plain
{{ my_list|first|upper }}
```

以上实例将第一个元素并将其转化为大写。

有些过滤器有参数。 过滤器的参数跟随冒号之后并且总是以双引号包含。 例如：

```plain
{{ bio|truncatewords:"30" }}
```

这个将显示变量 bio 的前30个词。

其他过滤器：

+ addslashes : 添加反斜杠到任何反斜杠、单引号或者双引号前面。
+ date : 按指定的格式字符串参数格式化 date 或者 datetime 对象，实例：

```plain
{{ pub_date|date:"F j, Y" }}
```

+ length : 返回变量的长度。

**default**

default 为变量提供一个默认值。

如果 views 传的变量的布尔值是 false，则使用指定的默认值。

#### **管理工具**
**/HelloWorld/HelloWorld/urls.py** 

url('admin/',admin.site.urls),

启动开发服务器，然后在浏览器中访问 [http://127.0.0.1:8000/admin/，得到如下界面：](http://127.0.0.1:8000/admin/，得到如下界面：)

你可以通过命令 **python manage.py createsuperuser** 来创建超级用户，如下所示：

为了让 admin 界面管理某个数据模型，我们需要先注册该数据模型到 admin。比如，我们之前在 TestModel 中已经创建了模型 Test 。修改 TestModel/admin.py:

```plain
from django.contrib import admin
from TestModel.models import Test
 
# Register your models here.
admin.site.register(Test)
```

刷新后即可看到 Testmodel 数据表:







# 2.ORM
### 1. 什么是 ORM 框架？
**ORM** 的全称是 **Object-Relational Mapping**，即 **对象-关系映射**。

它的核心思想是：**在面向对象的编程语言中，用一种近乎自然的方式，使用类和对象来操作和操纵关系型数据库，而无需编写繁琐的 SQL 语句。**

你可以把它想象成一个**翻译官**或**桥梁**：

+ **程序这一端**：你使用 Python（Django）、Java（Spring Boot）等面向对象语言。你思考的是“对象”、“类”、“属性”、“方法”。
+ **数据库那一端**：是 MySQL、PostgreSQL、SQLite 等关系型数据库。它只理解“表”、“行”、“列”、“SQL”。

**ORM 的作用就是让这两端能够沟通：**

+ 将数据库中的一张 **表** 映射为程序中的一个 **类**。
+ 将表中的一 **行** 数据映射为类的一个 **实例/对象**。
+ 将表中的一 **列** 字段映射为对象的一个 **属性**。

**举个例子：**  
假设我们有一张 `users` 表：

| id | username | email |
| :--- | :--- | :--- |
| 1 | alice | [alice@mail.com](https://mailto:alice@mail.com/) |
| 2 | bob | [bob@mail.com](https://mailto:bob@mail.com/) |


**没有 ORM 时**，你查询用户 `alice` 需要这样写（以 Python 为例）：

python

```plain
# 1. 编写SQL字符串
sql = "SELECT * FROM users WHERE username = %s;"
# 2. 建立数据库连接、游标
cursor.execute(sql, ('alice',))
# 3. 处理返回的元组结果
result = cursor.fetchone()
user_id = result[0]
username = result[1]
email = result[2]
```



**有 ORM 时**，你可能会这样定义一个 `User` 类，然后这样操作：

python

```plain
# 1. 定义模型（ORM负责创建对应的表）
class User:
    id = IntegerField()
    username = CharField()
    email = CharField()

# 2. 查询操作（ORM会自动生成并执行对应的SQL）
user = User.objects.get(username='alice') # 返回一个User对象
print(user.username) # 直接访问属性，输出：alice
print(user.email)    # 直接访问属性，输出：alice@mail.com
```













**ORM 的优势：**

1. **提升开发效率**：无需编写大量重复的 SQL，代码更简洁。
2. **代码可读性高**：操作对象比操作 SQL 字符串更符合程序员的思维。
3. **数据库无关性**：只需简单配置即可切换底层数据库（如从 MySQL 换到 PostgreSQL），代码几乎不用改动。
4. **安全性**：ORM 通常使用参数化查询，能有效防止 SQL 注入攻击。

**ORM 的劣势：**

1. **性能损耗**：自动生成的 SQL 可能不是最优的，对于极端复杂的查询，手写 SQL 性能更高。
2. **学习成本**：需要学习 ORM 的特定语法。
3. **复杂查询**：对于非常复杂的多表关联和查询，用 ORM 表达可能不如 SQL 直观。

---

### 2. 什么是 SQLAlchemy？
**SQLAlchemy** 是 Python 社区中一个功能极其强大、灵活且流行的 **独立 ORM 框架**。

它的核心特点是 **“强大”** 和 **“灵活”**。

+ **独立性**：它不依赖于任何一个 Web 框架，你可以在任何 Python 项目（Flask、FastAPI、爬虫脚本等）中使用它。
+ **两层结构**：
    1. **Core**：一个数据库抽象层（DBAL），提供了 SQL 表达式语言，允许你以 Python 的方式构建 SQL 语句。即使不用它的 ORM 部分，这个 Core 也非常强大。
    2. **ORM**：建立在 Core 之上的高级、纯 Python 的 ORM。这才是我们通常所说的“用 SQLAlchemy 做 ORM”。

**使用 SQLAlchemy ORM 的示例：**

python

```plain
from sqlalchemy import Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

# 定义模型类
class User(Base):
    __tablename__ = 'users' # 指定表名

    id = Column(Integer, primary_key=True)
    username = Column(String)
    email = Column(String)

# 查询
user = session.query(User).filter_by(username='alice').first()
```









---

### 3. SQLAlchemy 与 Django 的关系是什么？
这是一个关键问题。它们的关系可以概括为：**Django 内置了自己的 ORM，但你可以选择用 SQLAlchemy 替换它。**

#### 1. Django 的内置 ORM
+ **Django ORM** 是 Django 框架的一个 **紧密集成** 的组成部分。当你创建一个 Django 项目时，它已经为你准备好了 ORM。
+ 它的特点是 **“简单”** 和 **“易用”**。其 API 设计非常直观，遵循 Django 的“约定优于配置”的原则，能让开发者快速上手。
+ Django 的很多核心功能（如 Admin 后台、通用视图、表单）都深度依赖其内置的 ORM。它们是天作之合。

**使用 Django ORM 的示例：**

python

```plain
# 在 models.py 中定义模型
from django.db import models

class User(models.Model):
    username = models.CharField(max_length=100)
    email = models.EmailField()

# 查询
user = User.objects.get(username='alice')
```



#### 2. Django 与 SQLAlchemy 的协作与选择
+ **默认情况**：Django 项目 100% 使用其内置的 ORM。
+ **为何考虑 SQLAlchemy**：
    - 当你的业务逻辑非常复杂，需要执行极其灵活和高级的数据库操作时，SQLAlchemy 的表达式语言和更强大的关系映射能力可能更合适。
    - 当你从一个使用 SQLAlchemy 的其他项目（如 Flask）迁移到 Django，希望重用部分模型代码时。
+ **如何协作**：在同一个 Django 项目中，你**可以同时使用两者**。
    - 使用 Django ORM 来处理与 Django 深度集成的部分（如 Admin、用户认证）。
    - 在需要复杂数据处理的其他独立模块中，引入并单独配置 SQLAlchemy 来使用。

#### 3. 总结关系
| 特性 | Django ORM | SQLAlchemy ORM |
| :--- | :--- | :--- |
| **定位** | Django 框架的**内置组件** | **独立**的第三方库 |
| **哲学** | 简单、易用、约定优于配置 | 强大、灵活、功能全面 |
| **灵活性** | 相对较低，与 Django 深度绑定 | **极高**，可单独使用，也可与任何框架集成 |
| **学习曲线** | 平缓，适合初学者和快速开发 | 相对陡峭，功能多，概念复杂 |
| **适用场景** | 标准的 Web 应用、快速开发 | 需要复杂数据库操作、高性能要求的项目 |


**结论：**

+ **对于典型的 Django 项目**，强烈建议使用其**内置的 ORM**，因为它能提供最好的开发体验和框架集成度。
+ 只有在你遇到 Django ORM 无法满足的**特定高级需求**时，才应考虑在 Django 项目中引入 SQLAlchemy 作为补充。
+ 如果把 Django 比作一辆**全家出游的 SUV**（开箱即用，舒适省心），那么 SQLAlchemy 就像一套**专业的汽车改装工具**（功能强大，但需要你自己动手组装和调校）。

## 数据库模型设计
### 1. 用户模型 (users/models.py)
python

```plain
from django.contrib.auth.models import AbstractUser
from django.db import models

class User(AbstractUser):
    ROLE_CHOICES = (
        ('admin', '管理员'),
        ('user', '普通用户'),
    )
    STATUS_CHOICES = (
        ('active', '正常'),
        ('disabled', '禁用'),
    )
    
    role = models.CharField(max_length=10, choices=ROLE_CHOICES, default='user')
    status = models.CharField(max_length=10, choices=STATUS_CHOICES, default='active')
    phone = models.CharField(max_length=15, blank=True, null=True)
    max_borrow_count = models.IntegerField(default=5)  # 最大借阅数量
    
    class Meta:
        db_table = 'users'
        indexes = [
            models.Index(fields=['username']),
            models.Index(fields=['role', 'status']),
        ]

class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    borrowed_count = models.IntegerField(default=0)  # 当前借阅数量
    total_borrowed = models.IntegerField(default=0)  # 历史借阅总数
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'user_profiles'
```



### 2. 图书模型 (books/models.py)
python

```plain
from django.db import models

class Category(models.Model):
    name = models.CharField(max_length=100, unique=True)
    description = models.TextField(blank=True)
    
    class Meta:
        db_table = 'categories'
        verbose_name_plural = 'categories'

class Book(models.Model):
    isbn = models.CharField(max_length=13, unique=True)
    title = models.CharField(max_length=200)
    author = models.CharField(max_length=100)
    category = models.ForeignKey(Category, on_delete=models.PROTECT)
    publisher = models.CharField(max_length=100)
    publish_date = models.DateField()
    total_copies = models.IntegerField(default=1)  # 总库存
    available_copies = models.IntegerField(default=1)  # 可借库存
    price = models.DecimalField(max_digits=8, decimal_places=2, default=0.0)
    description = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'books'
        indexes = [
            models.Index(fields=['isbn']),
            models.Index(fields=['title']),
            models.Index(fields=['author']),
            models.Index(fields=['category']),
        ]
    
    def __str__(self):
        return f"{self.title} - {self.author}"
```

### . 借阅记录模型 (borrow/models.py)
python

```plain
from django.db import models
from django.utils import timezone

class BorrowRecord(models.Model):
    STATUS_CHOICES = (
        ('pending', '待审核'),
        ('borrowed', '借阅中'),
        ('returned', '已归还'),
        ('overdue', '超期未还'),
        ('rejected', '已拒绝'),
    )
    
    user = models.ForeignKey('users.User', on_delete=models.CASCADE)
    book = models.ForeignKey('books.Book', on_delete=models.CASCADE)
    borrow_date = models.DateTimeField(auto_now_add=True)
    expected_return_date = models.DateTimeField()  # 预计归还时间
    actual_return_date = models.DateTimeField(null=True, blank=True)  # 实际归还时间
    status = models.CharField(max_length=10, choices=STATUS_CHOICES, default='pending')
    overdue_days = models.IntegerField(default=0)  # 超期天数
    fine_amount = models.DecimalField(max_digits=8, decimal_places=2, default=0.0)  # 罚款金额
    
    class Meta:
        db_table = 'borrow_records'
        indexes = [
            models.Index(fields=['user', 'status']),
            models.Index(fields=['book', 'status']),
            models.Index(fields=['expected_return_date']),
            models.Index(fields=['status']),
        ]
```

## 业务逻辑层设计 (Service Layer)
### 借阅服务 (borrow/services.py)
python

```plain
from django.utils import timezone
from datetime import timedelta
from django.db import transaction
from django.core.exceptions import ValidationError
from .models import BorrowRecord
from apps.books.models import Book
from apps.users.models import User

class BorrowService:
    MAX_BORROW_COUNT = 5
    MAX_BORROW_DAYS = 30
    FINE_PER_DAY = 0.5
    
    @staticmethod
    def can_user_borrow(user):
        """检查用户是否可以借书"""
        if user.status != 'active':
            raise ValidationError('用户账户已被禁用')
        
        current_borrowed = BorrowRecord.objects.filter(
            user=user, 
            status__in=['borrowed', 'overdue']
        ).count()
        
        if current_borrowed >= user.max_borrow_count:
            raise ValidationError(f'已达到最大借阅数量 {user.max_borrow_count} 本')
        
        return True
    
    @staticmethod
    @transaction.atomic
    def borrow_book(user, book_id):
        """借书业务逻辑"""
        try:
            book = Book.objects.select_for_update().get(id=book_id)
            
            # 检查库存
            if book.available_copies <= 0:
                raise ValidationError('该书暂无库存')
            
            # 检查用户借阅资格
            BorrowService.can_user_borrow(user)
            
            # 创建借阅记录
            expected_return = timezone.now() + timedelta(days=BorrowService.MAX_BORROW_DAYS)
            borrow_record = BorrowRecord.objects.create(
                user=user,
                book=book,
                expected_return_date=expected_return,
                status='borrowed' if user.role == 'user' else 'pending'
            )
            
            # 更新图书库存
            book.available_copies -= 1
            book.save()
            
            # 更新用户借阅计数
            user_profile = user.userprofile
            user_profile.borrowed_count += 1
            user_profile.save()
            
            return borrow_record
            
        except Book.DoesNotExist:
            raise ValidationError('图书不存在')
    
    @staticmethod
    @transaction.atomic
    def return_book(borrow_record_id):
        """还书业务逻辑"""
        try:
            record = BorrowRecord.objects.select_related('book', 'user').get(id=borrow_record_id)
            
            if record.status not in ['borrowed', 'overdue']:
                raise ValidationError('该借阅记录无法归还')
            
            # 更新记录状态
            record.actual_return_date = timezone.now()
            record.status = 'returned'
            
            # 计算超期罚款
            if record.actual_return_date > record.expected_return_date:
                overdue_days = (record.actual_return_date - record.expected_return_date).days
                record.overdue_days = overdue_days
                record.fine_amount = overdue_days * BorrowService.FINE_PER_DAY
            
            record.save()
            
            # 恢复图书库存
            book = record.book
            book.available_copies += 1
            book.save()
            
            # 更新用户借阅计数
            user_profile = record.user.userprofile
            user_profile.borrowed_count -= 1
            user_profile.total_borrowed += 1
            user_profile.save()
            
            return record
            
        except BorrowRecord.DoesNotExist:
            raise ValidationError('借阅记录不存在')

class StatisticsService:
    """统计服务"""
    
    @staticmethod
    def get_top_books(days=30, limit=10):
        """获取热门图书统计"""
        from django.db.models import Count
        from datetime import timedelta
        
        start_date = timezone.now() - timedelta(days=days)
        
        return BorrowRecord.objects.filter(
            borrow_date__gte=start_date,
            status__in=['borrowed', 'returned']
        ).values(
            'book__title', 'book__author'
        ).annotate(
            borrow_count=Count('id')
        ).order_by('-borrow_count')[:limit]
    
    @staticmethod
    def get_user_activity_ranking(limit=10):
        """用户活跃度排行"""
        from django.db.models import Count
        
        return User.objects.filter(
            borrowrecord__status__in=['borrowed', 'returned']
        ).annotate(
            total_borrows=Count('borrowrecord')
        ).order_by('-total_borrows')[:limit]
```

## 视图层设计 (Controller Layer)
### API 视图示例 (borrow/views.py)
python

```plain
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from django.utils.decorators import method_decorator
from django.views.decorators.cache import cache_page

from .models import BorrowRecord
from .services import BorrowService, StatisticsService
from .serializers import BorrowRecordSerializer
from utils.decorators import role_required

class BorrowViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    serializer_class = BorrowRecordSerializer
    
    def get_queryset(self):
        user = self.request.user
        if user.role == 'admin':
            return BorrowRecord.objects.all().select_related('user', 'book')
        else:
            return BorrowRecord.objects.filter(user=user).select_related('book')
    
    @action(detail=False, methods=['post'])
    def borrow(self, request):
        """借书接口"""
        book_id = request.data.get('book_id')
        try:
            borrow_record = BorrowService.borrow_book(request.user, book_id)
            serializer = self.get_serializer(borrow_record)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)
    
    @action(detail=True, methods=['post'])
    def return_book(self, request, pk=None):
        """还书接口"""
        try:
            borrow_record = BorrowService.return_book(pk)
            serializer = self.get_serializer(borrow_record)
            return Response(serializer.data)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)
    
    @method_decorator(role_required('admin'))
    @action(detail=True, methods=['post'])
    def approve(self, request, pk=None):
        """管理员审核借阅"""
        try:
            record = self.get_object()
            record.status = 'borrowed'
            record.save()
            serializer = self.get_serializer(record)
            return Response(serializer.data)
        except BorrowRecord.DoesNotExist:
            return Response({'error': '记录不存在'}, status=status.HTTP_404_NOT_FOUND)

class StatisticsViewSet(viewsets.ViewSet):
    permission_classes = [IsAuthenticated]
    
    @method_decorator(role_required('admin'))
    @method_decorator(cache_page(60 * 5))  # 缓存5分钟
    @action(detail=False, methods=['get'])
    def top_books(self, request):
        """热门图书统计"""
        days = request.query_params.get('days', 30)
        data = StatisticsService.get_top_books(days=int(days))
        return Response(data)
    
    @method_decorator(role_required('admin'))
    @action(detail=False, methods=['get'])
    def user_ranking(self, request):
        """用户活跃度排行"""
        data = StatisticsService.get_user_activity_ranking()
        return Response(data)
```

## 配置和工具类
### 权限装饰器 (utils/decorators.py)
python

```plain
from functools import wraps
from django.http import JsonResponse

def role_required(role):
    def decorator(view_func):
        @wraps(view_func)
        def wrapped_view(request, *args, **kwargs):
            if request.user.role != role:
                return JsonResponse(
                    {'error': '权限不足'}, 
                    status=403
                )
            return view_func(request, *args, **kwargs)
        return wrapped_view
    return decorator
```



### 常量定义 (utils/constants.py)
python

```plain
# 借阅相关常量
MAX_BORROW_COUNT = 5
MAX_BORROW_DAYS = 30
FINE_PER_DAY = 0.5

# 状态常量
BORROW_STATUS = {
    'PENDING': 'pending',
    'BORROWED': 'borrowed', 
    'RETURNED': 'returned',
    'OVERDUE': 'overdue',
    'REJECTED': 'rejected'
}

USER_ROLES = {
    'ADMIN': 'admin',
    'USER': 'user'
}
```

## 数据库索引设计说明
### 主要索引：
1. **用户表**：`username`, `(role, status)`
2. **图书表**：`isbn`, `title`, `author`, `category_id`
3. **借阅记录表**：`(user_id, status)`, `(book_id, status)`, `expected_return_date`, `status`

### 性能优化：
+ 使用 `select_related` 和 `prefetch_related` 减少查询次数
+ 高频统计接口添加缓存
+ 使用数据库事务确保数据一致性

---

## 下一步实现建议
1. **先搭建基础框架**：创建Django项目和应用
2. **设计数据库模型**：实现上述三个核心模型
3. **实现用户认证**：基于Session或JWT
4. **开发核心业务**：借阅、归还、审核流程
5. **添加管理后台**：利用Django Admin快速搭建
6. **实现统计功能**：热门图书、用户排行等
7. **性能优化**：添加缓存、优化查询

controller/service/DAO分层架构

### **职责分离**
myproject/  
├── manage.py  
├── myproject/  
│   ├── **init**.py  
│   ├── settings.py  
│   ├── urls.py  
│   └── wsgi.py  
├── apps/  
│   ├── **init**.py  
│   └── book_app/                  # 图书应用  
│       ├── **init**.py  
│       ├── controllers/           # 控制层  
│       │   ├── **init**.py  
│       │   └── book_controller.py  
│       ├── services/              # 服务层  
│       │   ├── **init**.py  
│       │   └── book_service.py  
│       ├── daos/                  # 数据访问层  
│       │   ├── **init**.py  
│       │   └── book_dao.py  
│       ├── models/                # 模型层  
│       │   ├── **init**.py  
│       │   ├── entities.py        # 数据库实体  # SQLAlchemy 实体  
│       │   └── dtos.py            # 数据传输对象  

│       ├── database/              # 新增：SQLAlchemy 数据库配置  
│       │   ├── **init**.py  
│       │   ├── connection.py  
│       │   └── session.py

│       ├── exceptions/            # 自定义异常  
│       │   ├── **init**.py  
│       │   └── business_exceptions.py  
│       └── utils/                 # 工具类  
│           ├── **init**.py  
│           └── validators.py  
└── requirements.txt

    - ✅ Controller：只关心 HTTP 相关逻辑
    - ✅ Service：专注业务规则和流程
    - ✅ DAO：专注数据存储和检索

```plain
# Django ORM
from django.db import models

class User(models.Model):
    username = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'auth_user'
        ordering = ['-created_at']

# SQLAlchemy
from sqlalchemy import Column, String, DateTime, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

Base = declarative_base()

class User(Base):
    __tablename__ = 'auth_user'
    
    username = Column(String(100))
    email = Column(String(120), unique=True)
    created_at = Column(DateTime, default=datetime.utcnow)
```

```plain
# Django ORM - 更简洁
from myapp.models import User

# 查询所有用户
users = User.objects.all()

# 条件查询
active_users = User.objects.filter(is_active=True)
john = User.objects.get(username='john')

# 链式调用
recent_users = User.objects.filter(
    created_at__gte='2024-01-01'
).exclude(
    is_active=False
).order_by('-created_at')[:10]

# SQLAlchemy - 更灵活
from sqlalchemy import and_, or_

# 查询所有用户
users = session.query(User).all()

# 条件查询
active_users = session.query(User).filter(User.is_active == True)
john = session.query(User).filter(User.username == 'john').first()

# 复杂查询
recent_users = session.query(User).filter(
    and_(
        User.created_at >= '2024-01-01',
        User.is_active == True
    )
).order_by(User.created_at.desc()).limit(10)
```

```plain
# Django ORM - 自动处理关联
class Author(models.Model):
    name = models.CharField(max_length=100)

class Book(models.Model):
    title = models.CharField(max_length=200)
    author = models.ForeignKey(Author, on_delete=models.CASCADE)

# 查询作者的所有书籍
author = Author.objects.get(name='鲁迅')
books = author.book_set.all()  # 自动反向查询

# 预取关联数据
books = Book.objects.select_related('author').all()

# SQLAlchemy - 需要显式定义关系
class Author(Base):
    __tablename__ = 'authors'
    id = Column(Integer, primary_key=True)
    name = Column(String(100))
    books = relationship("Book", back_populates="author")

class Book(Base):
    __tablename__ = 'books'
    id = Column(Integer, primary_key=True)
    title = Column(String(200))
    author_id = Column(Integer, ForeignKey('authors.id'))
    author = relationship("Author", back_populates="books")

# 查询作者的所有书籍
author = session.query(Author).filter(Author.name == '鲁迅').first()
books = author.books  # 通过关系属性访问

# 使用join查询
books = session.query(Book).join(Author).filter(Author.name == '鲁迅').all()
```

数据库迁移需要alembic

# ETC
#### # 自定义简单的登录保护装饰器（基于 Django 的 session）
```plain
def login_required(view_func):
    def wrapper(request, *args, **kwargs):
        user_id = request.session.get('user_id')  # 从session获取用户ID
        if not user_id:  # 如果用户未登录
            messages.error(request, '请先登录')  # 显示错误消息
            return redirect('login')  # 重定向到登录页面
        return view_func(request, *args, **kwargs)  # 如果已登录，执行原视图函数
    return wrapper
```

```plain
@login_required  # 使用装饰器
def my_view(request):
    # 这个视图需要登录才能访问
    return render(request, 'template.html')
```

**执行流程**

```plain
用户访问受保护页面 → 装饰器检查session → 
    ↓ 未登录                    ↓ 已登录
重定向到登录页面          执行原视图函数
```

**与Django内置装饰器的区别：**

| 特性 | 你的自定义装饰器 | Django的`@login_required` |
| :--- | :--- | :--- |
| 验证方式 | 检查`user_id` | 检查`request.user.is_authenticated` |
| 重定向路径 | `login` | `/accounts/login/` |
| 依赖 | 你自己的session系统 | Django用户系统 |


```plain
context = {
            'books': books,
            'total_books': total_books,
            'current_borrows': current_borrows,
            'overdue_count': overdue_count,
            'total_fines': total_fines,
            'my_borrowings': my_borrowings,
            'search_query': search_query,
            'users': user,
            'messages': messages.get_messages(request),
        }
在books上面添加注释后会出错,搜索不到图书
```

