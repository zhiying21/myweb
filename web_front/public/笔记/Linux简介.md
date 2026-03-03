# *  Linux介绍及使用

Linux是一种开源的、多用户的、多任务的操作系统，具有高度的稳定性、安全性和可定制性，被广泛应用于服务器、移动设备、科研等领域。（Windows11，macOS是个人桌面操作系统，而Linux是权威的服务器操作系统，操作系统是用户和计算机硬件的桥梁，是软件的一类）

<img src="D:\Huawei Share\Huawei Share\Linux.png" alt="Linux" style="zoom: 33%;" />



内核源码网址：https://www.kernel.org

####     Linux特点

- 开源免费：Linux的源代码是公开的，任何人都可以自由获取、使用、修改和分发，这使得它能够不断发展和完善。

- 多用户多任务：支持多个用户同时登录和使用系统，并且能让多个程序同时运行，每个用户都可以在自己的权限范围内进行操作。

- 稳定性高：Linux内核稳定，很少出现死机或系统崩溃的情况，适合长时间运行关键任务。

- 安全性强：具有良好的安全机制，包括用户权限管理、文件权限设置等，能够有效防止恶意攻击和数据泄露。

  #### 应用领域

  - 服务器领域：Linux在服务器市场占据重要地位，如网站服务器、邮件服务器、数据库服务器等，它能够高效处理大量并发请求，同时具有低成本和高可靠性的优势。

  - 嵌入式系统：在智能手机、路由器、智能家居设备等嵌入式设备中广泛应用，开发者可以根据设备需求对Linux进行定制裁剪。

  - 科研领域：许多科研机构和高校使用Linux进行科学计算、数据分析、模拟实验等，其强大的计算能力和可定制性能够满足科研工作的复杂需求。



![屏幕截图 2025-03-27 151946](C:\Users\1\Pictures\Screenshots\屏幕截图 2025-03-27 151946.png)

因为Linux的开源免费，诞生了很多版本，主流有Ubuntu和CentOS

因为将电脑重装系统为Linux不现实且Linux系统不适合日常办公，因此可以借助虚拟机来获得可用的Linux系统环境进行学习；

#### 虚拟机

​    安装：（仅供参考）虚拟硬件加操作系统构成虚拟机；可以使用VMware station Pro作为虚拟机，这里推荐从一个使用fanl.top  ->系统工具->VMWare 中选择版本下载(不用花钱，不用注册七七八八的账号)；centOS我下载用的是https://mirrors.aliyun.com/centos/7/isos/x86_64/（centOS是Linux的一个发行版）

#### 远程连接Linux操作系统：

1：图形化与命令行；在Linux开发中往往使用命令行形式，因为Linux的图形化页面不稳定，不好用，而命令行显示高效，直观，资源占用低，程序运行稳定；

2：finalShell：因为在VMware中操作Linux不太方便（主要包括内容复制粘贴和文件上传下载和各类系统交互需要跨过VMware）而可以通过finalshell这个软件；

3：链接：打开VMware，登入系统->右键选择打开终端->输入ifconfig，在ens33中找到inet行后面的IP，如192.168.31.128，然后在finalshell里新建Linux链接在主机中输入IP地址，填上公户名和密码即可连接到centOS系统；之后就可以在Finalshell里对centOS系统操作；

#### WSL

（Windows subsystem for Linux）可以更简单，更轻松的获得Linux操作系统环境，可以直接使用Windows系统，即宿主的硬件，

设置->启动或关闭windows功能->勾选适用于Linux的Windows子系统->关机；

应用商店下载Ubuntu（Linux的一个发行版）和windows terminal（终端软件）即可直接使用（只有命令行的形式）

#### 虚拟机快照

通过快照将虚拟机的状态保存下来，在以后可以通过快照恢复到虚拟机保存的状态（在虚拟机出现问题时可恢复所以不定期拍上快照是个好习惯）

操作：我的计算机->指定系统->快照->快照管理器->拍摄快照->填写名称和描述即可，之后可以在快照管理器中转到此快照即可回到当时的状态。（在虚拟机关机的状态下拍快照会更快）

#### Linux目录结构

Linux只有一个根目录 根目录下有文件夹a，a文件夹里有b，b里有c，则路径为/a/b/c

## Linux命令基础

命令行：即Linux终端（Terminal)提供字符化操作页面供命令使用<img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-10 192057.png" alt="屏幕截图 2025-04-10 192057" style="zoom: 67%;" />

例如：ls -l /home/a  ;ls是命令本身 -l是选项 /home/a是参数

意思是已列表的形式，显示home里a的内容

##### ls

ls语法：ls[-a -l -h] [Linux路径]；当不使用选项和参数直接使用ls命令本体时表示已平铺的形式列出当前工作目录下的内容；-a显示全部内容包括隐藏选项，-l列项排列的同时显示更多信息，可以通过-l-a或-la或-al组合使用，-h以易于阅读的形式列出文件大小，-h必须和-l混合使用

<img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-10 195049.png" alt="屏幕截图 2025-04-10 195049" style="zoom:50%;" />

##### cd - pwd-命令

cd语法：cd Linux路径；作用是切换工作目录

pwd命令无参数，用于显示当前工作目录

##### 相对路径和绝对路径和特殊路径符

绝对路径：以根目录为起点，描述路径方式，以路径/开头

相对路径：以当前目录为起点；

" . "：表示当前目录，如cd ./Desktop或cd .

" .. ":表示上一级目录，比如：cd..或cd../..

" ~ ":表示用户的HOME目录，如：cd ~ 或cd ~/Desktop

Linux路径都可以用这三种方式表示

##### mkdir命令

mkdir [-p] Linux路径；用来创建新的目录（文件夹）

-p选填，当用于创建连续多层级目录时使用，如在a文件夹中没有b时        mkdir -p a/b/c ;表示a，b，c为依次包含关系

注：创建文件夹在没有权限时只能在home目录里创建文件夹

##### touch - cat - more命令

touch语法：touch Linux路径；用于创建一个新的文件,如test.txt

cat语法：cat Linux路径；用于查看文件的内容；

more语法：more Linux路径，与cat类似，用翻页的形式查看文件内容，按空格翻页，q退出查看

##### cp -mv -rm命令

cp命令：用于复制。语法：cp [-r] 参数1，参数2；-r在复制文件夹时选填，路径1表示被复制的文件，路径2表示要去的地方（最末尾的路径表示该文件的名字）

mv命令：用于移动文件或文件夹。语法：mv 路径1 路径2；路径2表示要去的地方，当没有该路径时就起一个改名的作用

rm命令：删除文件或文件夹。rm [-r -f] 参数1，2，... ，n；-r的用法和cp类似，-f用于强制删除（一般用于root用户，在root权限下用rm会有提示，用-f就没有提示了）多个路径表示可以删除多个文件或文件夹

###### root用户

可以通过su - root，并输入密码，可以临时切换到root用户，输入exit命令可以退回普通用户。在root权限下，输入rm -rf /或rm -rf /*，将会直接毁掉Linux操作系统，命令都会失效，这时候可以通过虚拟机快照或重新装配Linux操作系统来复原；

###### 通配符的使用“ * ”

在rm命令的参数可以使用通配符进行模拟匹配，如test*，表示所有含有test前缀的文件，同理 *test *， *test，

##### which-find命令

which命令用于查找命令的程序文件，which语法：which 要查找的命令；可以用来查找命令所在的位置 

find命令，用来查找文件，按文件名查找：find 起始路径 -name "被查找的文件名"（支持通配符的使用）例如find / -name "test*"；按文件大小查找：find 起始路径 -size +|- [KMG]；加表示大于，减表示小于，K/M/G分别表示文件大小,例如find / -size +10k;

##### grep-wc-管道符的使用

grep用来通过关键字过滤文件行；语法：grep [-n] 关键字 文件路径；-n选用时可以显示匹配的行号，关键字建议用" "包围

wc命令用来统计文件行数，单词数量等；语法 wc [-c -m -l -w]文件路径，-c 统计bytes数，-m统计字符数，-l统计行数，-w统计单词数

管道符 " | ":将左边的结果作为右边命令的输入，如 grep "CPP" test.txt等价于cat test.txt | grep CPP。ls -l /usr/bin | wc -l

##### echo - tail -重定向符

echo语法：echo 输出的内容；相当于C++的cout

当 echo `pwd`是起pwd的功能，因为反引号“``”的作用是将被包围的内容作为命令被执行

重定向符

" > "将左侧命令的结果，覆盖写入符号右侧的文件中

" >> "将左侧命令的结果，追加写入符号右侧的文件中

tail命令语法 tail [-f -num] Linux路径；作用是查看文件尾部内容，跟踪文件就改，-f选用时可以实时跟踪，-num不填时默认尾部后十行

例：echo "`pwd`">test.txt,表示将当前的工作目录输入到test.txt中

#### vi\vim编辑器

![屏幕截图 2025-04-15 201906](C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-15 201906.png)

命令模式下的所有输入都被编译器理解为命令，以驱动不同的功能；

输入模式即编辑模式，可以对文件内容自由编辑；

底线命令模式，以`:`开始通常用于文件的保存和退出

vi/vim hello.txt(若不存在会创建一个新的文件来编辑)

命令模式快捷键

<img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-15 205626.png" alt="屏幕截图 2025-04-15 205626" style="zoom:50%;" /><img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-15 205734.png" alt="屏幕截图 2025-04-15 205734" style="zoom:50%;" /><img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-15 205827.png" alt="屏幕截图 2025-04-15 205827" style="zoom:50%;" /><img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-15 205542.png" alt="屏幕截图 2025-04-15 205542" style="zoom:50%;" />

## 用户与权限

su [-] [用户名];用于切换用户,'-'切换后加载环境变量，用户名默认为root；

sudo 其他命令，使后续的命令具有root权限，前提是先在root用户执行visudo命令；



再末尾输入'用户名 ALL=(ALL)   NOPASSWD: ALL'用wq保存即可

##### 用户与用户组

Linux对权限的管控分用户与用户组两个级别；

groupadd 用户组名；groupdel 用户组名；创建用户组；

useradd [-g -d] 用户名；-g指定用户的组，不指定时创建同名组加入；指定的组必须已存在；若存在同名组则必须使用-g。-d用于指定用户home路径，不指定则默认在/home/用户名；如 useradd test -g CPP -d /home/hh

userdel [-r] 用户名；不使用-r，删除的用户home目录会保留；

id[用户名]；用于查看用户的组

usermod -aG 用户组 用户名；用于将用户加的指定的组里面

getent passwd；可以查看系统中有哪些用户；七份信息分别是‘用户名:密码(x):用户ID:组ID:描述信息:home目录:执行终端’

getent group；可以查看系统中有哪些组

##### 权限信息

<img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-23 162212.png" alt="屏幕截图 2025-04-23 162212" style="zoom: 80%;" />

##### chmod命令

chmod [-R] 权限 文件或文件夹，-R选用时文件夹内所有文件的权限都会修改(只有文件所属用户和root用户可修改)例如：chmod -R u=rwx,g=rw,o=x hello.txt（chmod 761 hello。txt）

权限的数字序号

<img src="C:\Users\1\Pictures\Screenshots\屏幕截图 2025-04-23 170610.png" alt="屏幕截图 2025-04-23 170610" style="zoom:80%;" />

##### chown命令

chown [-R] [用户] [:] [用户组] 文件或文件夹;只有root用户才能执行，用于修改文件所属的用户和用户组

### Linux实用操作

##### 快捷键使用

ctrl+c用于强制停止程序运行，或重新输入；

ctrl+d退出账户的登入（也能退出如python（自带环境）程序的专属页面）

ctrl+l清屏

ctrl+r历史命令搜索

history查看历史命令

##### 软件安装

yum命令

yum[-y] [install | remove | search] 软件名称（使用需要联网）在Ubunto中yum换成apt，(一样的语法)

对应功能是下载，删除，寻找软件，-y无脑选；

##### systemctl命令

systemctl start | stop | status | eneable | disable 服务名。

作用分别是控制软件的 启动 | 关闭 | 查看状态 | 开启开机自 | 关闭开机自启。

系统内置服务：

networkManager，主服务网络；network 副服务网络；firewalled，防火墙服务；sshd，ssh服务(如finalshell远程登入)

适用于全部内置服务和部分第三方软件，如ntp(服务名是) 和 httpd，没有集成到systemctl中的第三方软件可以手动添加

##### 软链接

相当于window下的快捷方式 。 ln -s 参数1 参数2 ；-s表示建立软连接 参数1表示被链接的文件或文件夹，参数2表示要链接去的目的地

例如 ln -s /etc/yum.conf ~/yun.conf      ln -s /etc/yum ~/yum

生成的软连接可以在该目录下直接打开和执行

##### 日期与时区

date [-d] [+格式化字符串]；-d按照给定字符串显示日期。%Y年，%y年份后两位，%M月份，%d日 %H小时，%S秒 %s从1970-01-01-00.00到现在的秒数

在root权限下可以用一下代码更改时区

`rm -f /etc/localtime`

`ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime`

下载了ntp可以可以自启动`systemctl status ntpd`以定期校准时间

也可以手动校准`ntpdate -u ntp.aliyun.com`