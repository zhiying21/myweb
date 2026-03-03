# shell简介

## 1：什么是shell

### **1.1：shell是用户与操作系统内核之间的接口程序；**

> Shell 是一个应用程序，它连接了用户和 Linux 内核（中间商，翻译官），让用户能够更加高效、安全、低成本地使用 Linux 内核，这就是 Shell 的本质。

> Shell 程序本身的功能是很弱的，比如文件操作、输入输出、进程管理等都得依赖内核。我们运行一个命令，大部分情况下 Shell 都会去调用内核暴露出来的接口（这里的接口就是一个个函数），这就是在使用内核，只是这个过程被 Shell 隐藏了起来，它自己在背后默默进行，我们看不到而已。

> Shell还可以连接其他外部程序，可以无限扩展命令数量，其结果就是 Shell 的功能非常强大，完全能够胜任 Linux 的日常管理工作，如文本或字符串检索、文件的查找或创建、大规模软件的自动部署、更改系统设置、监控服务器性能、发送报警邮件、抓取网页内容、压缩文件等

### 1.2：**shell是一种脚本语言**

有的编程语言，如 C/C++、Pascal、Go语言、汇编等，必须在程序运行之前将所有代码都翻译成二进制形式，也就是生成可执行文件，用户拿到的是最终生成的可执行文件，看不到源码。

这个过程叫做编译（Compile），这样的编程语言叫做编译型语言，完成编译过程的软件叫做编译器（Compiler）。

而有的编程语言，如 Shell、[JavaScript](https://c.biancheng.net/js/)、Python、PHP等，需要一边执行一边翻译，不会生成任何可执行文件，用户必须拿到源码才能运行程序。程序运行后会即时翻译，翻译完一部分执行一部分，不用等到所有代码都翻译完。

这个过程叫做解释，这样的编程语言叫做解释型语言或者脚本语言（Script），完成解释过程的软件叫做解释器。

编译型语言的优点是执行速度快、对硬件要求低、保密性好，适合开发操作系统、大型应用程序、数据库等。

> 脚本语言的优点是使用灵活、部署容易、跨平台性好，非常适合 Web 开发以及小工具的制作。

Shell 就是一种脚本语言，我们编写完源码后不用编译，直接运行源码即可。

### 1.3：**shell解析器**

> 显示系统所有shell解析器

```[itlh@192 ~]$ cat /etc/shells
[itlh@192 ~]$ cat /etc/shells

/bin/sh

/bin/bash

/usr/bin/sh

/usr/bin/bash

/bin/tcsh

/bin/csh
```

> 查看当前系统解析器

```[itlh@192 ~]$ echo $SHELL
[itlh@192 ~]$ echo $SHELL
/bin/bash
```

> bash功能强大，一般是默认解析器

## 2：shell脚本编写格式与执行方式

### 2.1：编写格式

```
#!/bin/bash
```

> 首行需要设置shell解析器的类型

```shell
#注释内容
```

> 单行注释

```
:<<!
注释1
注释2
!
```

> 多行注释

```
touch helloworld.sh
vim helloworld.sh
```

> 创建第一个程序

### 2.2：脚本文件的3种执行方式

> sh解析器执行方式:本质用shell解析器运行脚本文件

```sh helloworld.sh
sh helloworld.sh
```

> bash解析器执行方式:bash是sh解析器的增强版，拥有更多的功能

```
bash helloworld.sh
```

> 仅路径执行方式:需要可执行权限

```
./helloworld.sh
```

### 2.3：编写简单脚本文件

> 在shell脚本文件中可以执行多命令处理

```
#!/bin/bash
touch /home/itlh/one.txt
echo "helloworld" >> /home/itlh/one.txt
```

> 在helloworld.sh文件中编写的命令“创建一个one.txt文件并对文件输入数据，可以直接对其他文件数据进行更改(可以对单机游戏开挂)

## 3：shell变量

### 3.1：自定义变量

#### 3.1.1：变量定义

> 在 Bash shell 中，每一个变量的值都是字符串，一般不会区分变量类型，在`=`两边不能有空格

````
variable=value
variable='value'
variable="value"
````

> 对应三种定义变量的方式

```
url=https://webvpn.scuec.edu.cn
echo $url
name='CPP'
echo $name
author="人"
echo $author
```

#### 3.1.2：变量使用

> 使用变量时只需要在变量名前面加上`$`符号即可

```
[itlh@192 ~]$ name=lh
[itlh@192 ~]$ echo MY NAME IS $name
MY NAME IS lh
[itlh@192 ~]$ echo MY NAME IS $name123
MY NAME IS
[itlh@192 ~]$ echo MY NAME IS ${name}123
MY NAME IS lh123
```

> 推荐给所有变量加上花括号`{ }`，这是个良好的编程习惯



> 变量删除

```
unset var_name
```

#### 3.1.3：自定义常量

> 变量设置值后不可更改的变量叫做常量，也叫自读变量

```shell
var_name=ls
readonly var_name
var_name=lh
```



### 3.2环境变量

#### 3.2.1：环境文件

**全局配置文件**

```
/etc/profile
/etc/profile.d/*.sh
/etc/bashrc
```

**个人配置文件**

```
当前用户/.bash_profile
当前用户/.bashrc
```

**两种环境变量**

> 系统级环境变量：shell环境加载全局配置文件中的变量共享给所有用户所有shell程序使用，全局共享
>
> 用户级环境变量：shell环境加载个人配置文件中的变量共享给当前用户shell程序使用，登入用户使用

```
env
```

> env可以查看当前shell系统环境变量

```
set
```

> 可以查看系统环境变量+自定义变量+函数

#### 3.2.2：自定义环境变量

> 在配置文件中自定义变量是加上export使其成为环境变量。

```bash
export MY_VAR="hello"
```

> 这样的变量会传递给当前shell的所有**子进程**，子进程中定义的变量无法传给父进程



> 其他独立的shell进程需要手动加载配置文件，且类似`export MY_VAR="hello"`代码必须写在特定的文件里面

```bash
source ~/.bashrc  #个人
source /etc/profile  #全局
```

#### 

**常用系统环境变量**

| 变量名称     | 含义                                                         |
| :----------- | ------------------------------------------------------------ |
| **PATH**     | 设置命令的搜索路径，以冒号分割；<br />比如在输入ls的时候系统就会在PATH包含的目录中找到ls程序 |
| HOME         | 当前用户主目录：/root                                        |
| SHELL        | 当前Shell解析器类型：/bin/bash                               |
| **HISTFILE** | 显示当前用户执行的历史列表文件：/root/.bash_history          |
| PWD          | 显示当前所在路径                                             |
| OLDPWD       | 显示之前的路径                                               |
| HOSTNAME     | 显示当前主机名                                               |
| HOSTTYPE     | 显示主机架构，是i386、i686，还是x86、x64等                   |
| **LANG**     | 设置当前系统语言环境：zh_CN.UTF-8                            |

### 3.3特殊变量

| 变量      | 含义                                                         |
| --------- | ------------------------------------------------------------ |
| $0        | 当前脚本的文件名。                                           |
| $n（n≥1） | 传递给脚本或函数的参数。n 是一个数字，表示第几个参数。例如，第一个参数是 $1，第二个参数是 $2。//n大于等于10时数字一定要用`{}`括起来 |
| $#        | 传递给脚本或函数的参数个数。                                 |
| $*        | 传递给脚本或函数的所有参数。获取的所有参数为一个字符串       |
| $@        | 传递给脚本或函数的所有参数。当被双引号`" "`包含时，$@获取的每一个参数都是独立的字符串，详情[Shell $*和$@的区别](https://c.biancheng.net/view/vip_4559.html) |
| $?        | 上个命令的退出状态，或函数的返回值，每个命令的返回值对应执行状态，可以用来检查脚本是否成功执行，非0代表执行失败，详情[Shell $?](https://c.biancheng.net/view/808.html) |
| $$        | 当前 Shell 进程 ID。对于 Shell 脚本，就是这些脚本所在的进程 ID。 |

**循环语法**

```
for var in 列表变量
do        #循环开始
    命令   #循环体
done      #循环结束
```

例如：

```demo.sh
for item in "$*"或者"$@"
do
   echo $item
done
```

```shell
sh demo.sh a b c  #在调用函数时可以传递参数，通过$n接收，也叫位置参数
```

> 带`$*`的输出为 
>
> a b c
>
> 带$@的输出为 
>
> a
>
> b
>
> c

