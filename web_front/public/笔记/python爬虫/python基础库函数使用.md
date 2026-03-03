## <font style="color:rgb(15, 17, 21);">1. requests 库 - 发送 HTTP 请求</font>
```plain
import requests

# GET 请求 获取数据
response = requests.get('https://api.github.com')
print(response.status_code)  # 状态码
print(response.text)         # 响应文本

# 带参数的 GET 请求
params = {'key1': 'value1', 'key2': 'value2'}
response = requests.get('https://httpbin.org/get', params=params)

# POST 请求 提交/创建数据
data = {'username': 'user', 'password': 'pass'}
response = requests.post('https://httpbin.org/post', data=data)

# 带 JSON 数据的 POST 请求
json_data = {'name': 'John', 'age': 30}
response = requests.post('https://httpbin.org/post', json=json_data)

# 设置请求头,告诉服务器如何处理请求
headers = {'User-Agent': 'my-app/1.0'}
response = requests.get('https://api.github.com', headers=headers)
```

requests发送get请求,服务器的响应存储到response中

## <font style="color:rgb(15, 17, 21);">GET带参数的实际应用场景</font>
### <font style="color:rgb(15, 17, 21);">1. API 分页查询GET</font>
<font style="color:rgb(15, 17, 21);">python</font>

```plain
def get_users(page=1, per_page=20):
    params = {
        'page': page,
        'per_page': per_page,
        'sort': 'created',
        'direction': 'desc'
    }
    response = requests.get('https://api.github.com/users', params=params)
    return response.json()

users = get_users(page=1, per_page=10)
```

### <font style="color:rgb(15, 17, 21);">2. 搜索功能</font>
<font style="color:rgb(15, 17, 21);">python</font>

```plain
def search_products(query, category=None, min_price=None, max_price=None):
    params = {'q': query}
    
    if category:
        params['category'] = category
    if min_price is not None:
        params['min_price'] = min_price
    if max_price is not None:
        params['max_price'] = max_price
        
    response = requests.get('https://api.example.com/products', params=params)
    return response.json()

results = search_products('laptop', category='electronics', min_price=1000)
```

## <font style="color:rgb(15, 17, 21);">POST </font>**<font style="color:rgb(15, 17, 21);">实际应用场景</font>**
### <font style="color:rgb(15, 17, 21);">1.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">用户注册</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
def register_user(username, email, password):
    user_data = {
        'username': username,
        'email': email,
        'password': password
    }
    response = requests.post('https://api.example.com/register', json=user_data)
    
    if response.status_code == 201:
        print("用户注册成功!")
        return response.json()  # 返回用户ID等信息
    else:
        print("注册失败:", response.json())
        return None

# 使用
new_user = register_user('john_doe', 'john@example.com', 'secure123')
```

### <font style="color:rgb(15, 17, 21);">2.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">创建博客文章</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
def create_blog_post(title, content, tags, author_id):
    post_data = {
        'title': title,
        'content': content,
        'tags': tags,
        'author_id': author_id,
        'status': 'published'
    }
    response = requests.post('https://api.example.com/posts', json=post_data)
    return response.json()

# 使用
new_post = create_blog_post(
    title='Python学习指南',
    content='这是文章内容...',
    tags=['python', '编程', '教程'],
    author_id=123
)
```

### <font style="color:rgb(15, 17, 21);">3.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">提交订单</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
def place_order(customer_id, items, shipping_address):
    order_data = {
        'customer_id': customer_id,
        'items': items,  # 商品列表
        'shipping_address': shipping_address,
        'total_amount': sum(item['price'] * item['quantity'] for item in items)
    }
    response = requests.post('https://api.example.com/orders', json=order_data)
    return response.json()

# 使用
order = place_order(
    customer_id=456,
    items=[
        {'product_id': 1, 'name': 'Python书', 'price': 59.99, 'quantity': 2},
        {'product_id': 2, 'name': '鼠标', 'price': 129.99, 'quantity': 1}
    ],
    shipping_address={'street': '123 Main St', 'city': 'Beijing'}
)
```

### <font style="color:rgb(15, 17, 21);">4. </font>**<font style="color:rgb(15, 17, 21);">发送消息/通知</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
def send_message(sender, receiver, message, message_type='text'):
    message_data = {
        'sender': sender,
        'receiver': receiver,
        'message': message,
        'type': message_type,
        'timestamp': '2024-01-01T10:30:00Z'
    }
    response = requests.post('https://api.example.com/messages', json=message_data)
    return response.status_code == 200

# 使用
send_message('user123', 'user456', '你好！最近怎么样？')
```

## **<font style="color:rgb(15, 17, 21);">请求头的基本作用</font>**
### <font style="color:rgb(15, 17, 21);">1. </font>**<font style="color:rgb(15, 17, 21);">User-Agent - 身份标识</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
# 标识客户端身份
headers = {
    'User-Agent': 'my-app/1.0 (https://myapp.com; contact@myapp.com)'
}
response = requests.get('https://httpbin.org/user-agent', headers=headers)
print(response.json())
# 输出: {'user-agent': 'my-app/1.0 (https://myapp.com; contact@myapp.com)'}

# 不同场景的 User-Agent
user_agents = {
    'chrome': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    'mobile': 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)',
    'custom_app': 'MyAnalyticsBot/1.0 (+https://myapp.com/bot)'
}
```

### <font style="color:rgb(15, 17, 21);">2.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">认证和授权</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
# API Token 认证
headers = {
    'Authorization': 'Bearer your_api_token_here',
    'User-Agent': 'my-app/1.0'
}
response = requests.get('https://api.github.com/user', headers=headers)

# Basic 认证
import base64
credentials = base64.b64encode(b'username:password').decode('utf-8')
headers = {'Authorization': f'Basic {credentials}'}
```

### <font style="color:rgb(15, 17, 21);">3.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">内容协商</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
# 告诉服务器客户端期望的数据格式
headers = {
    'Accept': 'application/json',  # 期望JSON响应
    'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',  # 语言偏好
    'Accept-Encoding': 'gzip, deflate'  # 压缩格式
}
response = requests.get('https://api.example.com/data', headers=headers)
```

### <font style="color:rgb(15, 17, 21);">4.</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">缓存控制</font>**
<font style="color:rgb(15, 17, 21);">python</font>

```plain
# 控制缓存行为
headers = {
    'Cache-Control': 'no-cache',  # 不要使用缓存
    'If-None-Match': 'etag_value'  # 如果内容未改变返回304
}
response = requests.get('https://api.example.com/data', headers=headers)
```

## <font style="color:rgb(15, 17, 21);">2. json 库 - JSON 数据处理</font>
<font style="color:rgb(15, 17, 21);">python</font>

```plain
import json

# Python 对象转换为 JSON 字符串
data = {
    'name': 'Alice',
    'age': 25,
    'hobbies': ['reading', 'swimming']
}
json_string = json.dumps(data)
print(json_string)

# JSON 字符串转换为 Python 对象
python_obj = json.loads(json_string)
print(python_obj['name'])

# 写入 JSON 文件
with open('data.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)

# 读取 JSON 文件
with open('data.json', 'r', encoding='utf-8') as f:
    loaded_data = json.load(f)
    print(loaded_data)
```

**json文件轻量,数据持久化,多语言兼容,简洁 **



## <font style="color:rgb(15, 17, 21);">3. re 库 - 正则表达式</font>
```plain
import re

# 查找匹配
text = "我的电话是 138-1234-5678，另一个是 139-8765-4321"
pattern = r'\d{3}-\d{4}-\d{4}'
#    解释: \d{3} - \d{4} - \d{4}
#         3个数字 - 4个数字 - 4个数字

# 查找所有匹配
matches = re.findall(pattern, text)
print(matches)  # ['138-1234-5678', '139-8765-4321']

# 搜索第一个匹配
match = re.search(pattern, text)
if match:
    print(match.group())  # 138-1234-5678

# 替换
new_text = re.sub(pattern, '***-****-****', text)
print(new_text)  # 我的电话是 ***-****-****，另一个是 ***-****-****

# 分割
text = "apple,banana;orange:grape"
result = re.split(r'[,;:]', text)
print(result)  # ['apple', 'banana', 'orange', 'grape']
```

##  4.bs4库
pip install bs4

```plain
import requests
from bs4 import BeautifulSoup
headers ={
    "User-Agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0"
}
for page in range(0,250,25):
    responses = requests.get(f"https://movie.douban.com/top250?start={page}",headers=headers)
    html = responses.text
    soup = BeautifulSoup(html,"html.parser")
    all_title = soup.findAll("span" ,attrs={"class": "title"})
    for title in all_title:
        title_string=title.string
        if "/"not in title.string:
            print(title.string)
```

