**git使用场景**  
1.备份;

2.代码还原

3.协同开发

4.追溯代码编写时间

****

**版本控制器的方式**

Git是一个分布式版本控制工具 ,每个人的电脑上都是一个完整的版本库,多人协作只需要各自的修改推送给对方  


**常用代码**

# **项目经理创建主仓库**
**git init --bare library_system.git**

# **团队成员克隆仓库**
**git clone git@github.com:yourteam/library_system.git  
****cd library_system**

# **查看所有分支**
**git branch -a**

# **创建功能分支**
**git checkout -b feature/user-authentication**

# **创建修复分支**
**git checkout -b fix/login-bug**

# **创建发布分支**
**git checkout -b release/v1.2.0**

****

****

### <font style="color:rgb(15, 17, 21);">开发新功能流程</font>
# **1. 从开发分支创建功能分支**
**git checkout develop  
****git pull origin develop  
****git checkout -b feature/admin-dashboard**

# **2. 开发功能，多次提交**
**git add .  
****git commit -m "feat: 添加管理员仪表盘页面"  
****git commit -m "feat: 实现图书统计图表"  
****git commit -m "fix: 修复图表显示问题"**

# **3. 推送到远程**
**git push -u origin feature/admin-dashboard**

# **4. 在GitHub/GitLab创建Pull Request**


协同操作的实现

### <font style="color:rgb(15, 17, 21);">核心概念：先理解“怎么工作”再知道“怎么操作”</font>
<font style="color:rgb(15, 17, 21);">在学具体命令前，先理解 Git 协同的工作流至关重要。最经典也最常用的模型是</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">功能分支工作流</font>**<font style="color:rgb(15, 17, 21);">。</font>

**<font style="color:rgb(15, 17, 21);">核心思想：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">主分支（通常是</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">main</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">或</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">master</font>`<font style="color:rgb(15, 17, 21);">）的代码必须时刻保持稳定、可发布的状态。任何新功能、修复bug的工作都必须在独立的</font>**<font style="color:rgb(15, 17, 21);">分支</font>**<font style="color:rgb(15, 17, 21);">上完成，完成后再合并回主分支。</font>

---

### <font style="color:rgb(15, 17, 21);">第一步：准备工作 - 配置与克隆</font>
1. **<font style="color:rgb(15, 17, 21);">安装 Git</font>**<font style="color:rgb(15, 17, 21);">  
</font><font style="color:rgb(15, 17, 21);">从</font><font style="color:rgb(15, 17, 21);"> </font>[<font style="color:rgb(57, 100, 254);">git-scm.com</font>](https://git-scm.com/)<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">下载并安装。</font>

**<font style="color:rgb(15, 17, 21);">配置用户信息</font>**<font style="color:rgb(15, 17, 21);">  
</font><font style="color:rgb(15, 17, 21);">这是标识你身份的“签名”，每次提交都会用到。</font>

2. <font style="color:rgb(15, 17, 21);">bash</font>

```plain
git config --global user.name "你的用户名"
git config --global user.email "你的公司邮箱"
```

**<font style="color:rgb(15, 17, 21);">克隆远程仓库</font>**<font style="color:rgb(15, 17, 21);">  
</font><font style="color:rgb(15, 17, 21);">这是你加入一个已有项目的起点。你会把服务器上的代码库完整地下载到本地。</font>

3. <font style="color:rgb(15, 17, 21);">bash</font>

```plain
git clone <远程仓库的URL>
# 例如：git clone https://github.com/username/project.git
```

<font style="color:rgb(15, 17, 21);">这会在当前目录创建一个</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">project</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">文件夹，里面包含了整个项目代码和</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">.git</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">版本库。</font>

---

### <font style="color:rgb(15, 17, 21);">第二步：标准协同工作流（日常操作循环）</font>
<font style="color:rgb(15, 17, 21);">假设你要开发一个新功能</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">user-authentication</font>`<font style="color:rgb(15, 17, 21);">。</font>

#### <font style="color:rgb(15, 17, 21);">1. 同步最新代码</font>
<font style="color:rgb(15, 17, 21);">在开始任何新工作前，确保你的本地主分支是最新的。</font>

<font style="color:rgb(15, 17, 21);">bash</font>

```plain
# 切换到主分支
git checkout main

# 从远程仓库拉取最新的代码（等同于 git fetch + git merge）
git pull origin main
```

#### <font style="color:rgb(15, 17, 21);">2. 创建功能分支</font>
<font style="color:rgb(15, 17, 21);">基于最新的</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">main</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">分支创建一个你自己的分支。</font>**<font style="color:rgb(15, 17, 21);">这是协同开发的黄金法则！</font>**

<font style="color:rgb(15, 17, 21);">bash</font>

```plain
# 创建并切换到新分支
git checkout -b feature/user-authentication
```

<font style="color:rgb(15, 17, 21);">分支名要有意义，例如</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">feature/xxx</font>`<font style="color:rgb(15, 17, 21);">,</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">fix/xxx</font>`<font style="color:rgb(15, 17, 21);">,</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">hotfix/xxx</font>`<font style="color:rgb(15, 17, 21);">。</font>

#### <font style="color:rgb(15, 17, 21);">3. 在分支上进行开发</font>
<font style="color:rgb(15, 17, 21);">现在你可以在</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">feature/user-authentication</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">分支上自由地修改代码了。</font>

**<font style="color:rgb(15, 17, 21);">添加文件到暂存区：</font>**

+ <font style="color:rgb(15, 17, 21);">bash</font>

```plain
# 添加特定文件
git add src/auth.js
# 添加所有变化
git add .
```

**<font style="color:rgb(15, 17, 21);">提交更改到本地仓库：</font>**

+ <font style="color:rgb(15, 17, 21);">bash</font><font style="color:rgb(15, 17, 21);">git commit -m "feat: 实现用户登录逻辑"</font>

<font style="color:rgb(15, 17, 21);">提交信息要清晰，说明这次提交</font>**<font style="color:rgb(15, 17, 21);">做了什么</font>**<font style="color:rgb(15, 17, 21);">以及</font>**<font style="color:rgb(15, 17, 21);">为什么这么做</font>**<font style="color:rgb(15, 17, 21);">。</font>

<font style="color:rgb(15, 17, 21);">重复</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">add</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">和</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">commit</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">的步骤，直到功能完成。</font>

#### <font style="color:rgb(15, 17, 21);">4. 推送分支到远程仓库</font>
<font style="color:rgb(15, 17, 21);">将你的本地分支推送到远程（如 GitHub, GitLab），这样团队成员才能看到你的工作。</font>

<font style="color:rgb(15, 17, 21);">bash</font>

```plain
git push -u origin feature/user-authentication
# -u 参数是 --set-upstream 的简写，表示将本地分支与远程分支关联，下次只需 git push 即可
```

#### <font style="color:rgb(15, 17, 21);">5. 发起合并请求</font>
<font style="color:rgb(15, 17, 21);">这是协同操作的关键环节。</font>

1. <font style="color:rgb(15, 17, 21);">去 GitHub/GitLab 等代码托管平台。</font>
2. <font style="color:rgb(15, 17, 21);">你通常会看到一个提示，让你为你刚推送的分支创建一个</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">Pull Request</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">或</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">Merge Request</font>**<font style="color:rgb(15, 17, 21);">。</font>
3. <font style="color:rgb(15, 17, 21);">点击创建，填写清晰的标题和描述：</font>
    - **<font style="color:rgb(15, 17, 21);">做了什么：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">简要说明这个 PR 的功能。</font>
    - **<font style="color:rgb(15, 17, 21);">怎么做的：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">实现细节（可选，但对于复杂变更很重要）。</font>
    - **<font style="color:rgb(15, 17, 21);">测试：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">说明你做了哪些测试。</font>
    - **<font style="color:rgb(15, 17, 21);">相关Issue：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">关联项目管理系统中的任务（如</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">Closes #123</font>`<font style="color:rgb(15, 17, 21);">）。</font>
4. <font style="color:rgb(15, 17, 21);">指定</font>**<font style="color:rgb(15, 17, 21);">代码审查员</font>**<font style="color:rgb(15, 17, 21);">，请求你的同事来审查你的代码。</font>

#### <font style="color:rgb(15, 17, 21);">6. 代码审查与修改</font>
<font style="color:rgb(15, 17, 21);">团队成员会在 PR 页面上查看你的代码变更，并提出评论或修改建议。</font>

+ <font style="color:rgb(15, 17, 21);">根据反馈，在你的本地分支上继续修改。</font>
+ <font style="color:rgb(15, 17, 21);">再次</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">add</font>`<font style="color:rgb(15, 17, 21);">,</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">commit</font>`<font style="color:rgb(15, 17, 21);">。</font>
+ **<font style="color:rgb(15, 17, 21);">推送</font>**<font style="color:rgb(15, 17, 21);">到远程同名分支（</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git push</font>`<font style="color:rgb(15, 17, 21);">）。PR 会自动更新。</font>

#### <font style="color:rgb(15, 17, 21);">7. 合并与清理</font>
<font style="color:rgb(15, 17, 21);">当代码审查通过后，拥有合并权限的人（可能是你，也可能是项目负责人）会将该分支合并到</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">main</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">分支。通常使用</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">“Squash and Merge”</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">将多个提交合并成一个清晰的提交记录。</font>

<font style="color:rgb(15, 17, 21);">合并完成后：</font>

+ <font style="color:rgb(15, 17, 21);">删除远程的功能分支（在 PR 页面通常有按钮可以一键删除）。</font>

<font style="color:rgb(15, 17, 21);">切换回本地</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">main</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">分支，拉取最新代码，然后删除本地功能分支。</font>

+ <font style="color:rgb(15, 17, 21);">bash</font>

```plain
git checkout main
git pull origin main
git branch -d feature/user-authentication # 删除本地分支
```

---

### <font style="color:rgb(15, 17, 21);">第三步：处理冲突</font>
<font style="color:rgb(15, 17, 21);">冲突发生在当你和同事修改了同一文件的同一区域，并且 Git 无法自动合并时。</font>

**<font style="color:rgb(15, 17, 21);">场景：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">你正在</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">feature/A</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">分支开发，同事已经完成</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">feature/B</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">并合并到了</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">main</font>`<font style="color:rgb(15, 17, 21);">。你</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git pull origin main</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">想同步最新代码时，可能会发生冲突。</font>

**<font style="color:rgb(15, 17, 21);">解决步骤：</font>**

1. **<font style="color:rgb(15, 17, 21);">触发冲突：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">在合并或变基时，Git 会明确告诉你哪些文件有冲突（</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">CONFLICT (content)</font>`<font style="color:rgb(15, 17, 21);">）。</font>

**<font style="color:rgb(15, 17, 21);">手动编辑文件：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">打开冲突文件，你会看到类似这样的标记：</font>

2. <font style="color:rgb(15, 17, 21);">javascript</font>

```plain
<<<<<<< HEAD
// 你的代码
console.log("Hello from Feature A");
=======
// 同事的代码
console.log("Hello from Feature B");
>>>>>>> main
```

    - `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);"><<<<<<< HEAD</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">和</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">=======</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">之间是你的代码。</font>
    - `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">=======</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">和</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">>>>>>>> main</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">之间是远程分支（main）的代码。</font>

**<font style="color:rgb(15, 17, 21);">解决冲突：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">与同事沟通，决定保留哪一部分，或者进行整合。删除冲突标记。</font>

3. <font style="color:rgb(15, 17, 21);">javascript</font>

```plain
// 解决后的代码，例如整合两者
console.log("Hello from Feature A and B");
```

4. **<font style="color:rgb(15, 17, 21);">标记为已解决：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">解决完所有冲突后，使用</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git add <文件名></font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">告诉 Git 这个文件的冲突已经解决。</font>
5. **<font style="color:rgb(15, 17, 21);">完成合并：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">执行</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git commit</font>`<font style="color:rgb(15, 17, 21);">（如果是因为合并引起的冲突）来完成这次合并提交。</font>

---

### <font style="color:rgb(15, 17, 21);">最佳实践总结</font>
1. **<font style="color:rgb(15, 17, 21);">勤提交，少推送：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">本地提交可以频繁且细小，但推送到远程前要确保是一个相对完整的功能点。</font>
2. **<font style="color:rgb(15, 17, 21);">写清晰的提交信息：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">使用约定式提交（如</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">feat:</font>`<font style="color:rgb(15, 17, 21);">,</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">fix:</font>`<font style="color:rgb(15, 17, 21);">,</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">docs:</font>`<font style="color:rgb(15, 17, 21);">）是个好习惯。</font>
3. **<font style="color:rgb(15, 17, 21);">一个分支，一个任务：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">每个分支只做一件事，保持分支的纯粹性。</font>
4. **<font style="color:rgb(15, 17, 21);">及时同步主分支：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">经常从</font><font style="color:rgb(15, 17, 21);"> </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">main</font>`<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">拉取更新，避免在分支上落后太多，减少冲突的规模和难度。</font>
5. **<font style="color:rgb(15, 17, 21);">重视代码审查：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">PR/MR 是保证代码质量、知识传递和团队协作的关键环节。</font>
6. **<font style="color:rgb(15, 17, 21);">善用</font>****<font style="color:rgb(15, 17, 21);"> </font>**`**<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">.gitignore</font>**`**<font style="color:rgb(15, 17, 21);"> </font>****<font style="color:rgb(15, 17, 21);">文件：</font>**<font style="color:rgb(15, 17, 21);"> </font><font style="color:rgb(15, 17, 21);">忽略不需要版本控制的文件（如日志、依赖包、本地配置文件等）。</font>

### <font style="color:rgb(15, 17, 21);">常用命令速查表</font>
| <font style="color:rgb(15, 17, 21);">命令</font> | <font style="color:rgb(15, 17, 21);">作用</font> |
| --- | --- |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git clone <url></font>` | <font style="color:rgb(15, 17, 21);">克隆远程仓库</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git pull</font>` | <font style="color:rgb(15, 17, 21);">拉取远程更新并合并到当前分支</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git fetch</font>` | <font style="color:rgb(15, 17, 21);">仅拉取远程更新，不自动合并</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git checkout -b <branch></font>` | <font style="color:rgb(15, 17, 21);">创建并切换分支</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git add .</font>` | <font style="color:rgb(15, 17, 21);">添加所有变更到暂存区</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git commit -m "msg"</font>` | <font style="color:rgb(15, 17, 21);">提交变更到本地仓库</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git push -u origin <branch></font>` | <font style="color:rgb(15, 17, 21);">推送分支到远程（首次）</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git push</font>` | <font style="color:rgb(15, 17, 21);">推送变更到已关联的远程分支</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git status</font>` | <font style="color:rgb(15, 17, 21);">查看工作区和暂存区状态</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git log --oneline</font>` | <font style="color:rgb(15, 17, 21);">查看简洁的提交历史</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git merge <branch></font>` | <font style="color:rgb(15, 17, 21);">合并指定分支到当前分支</font> |
| `<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git branch -d <branch></font>` | <font style="color:rgb(15, 17, 21);">删除本地分支</font> |


<font style="color:rgb(15, 17, 21);">掌握了这个工作流，你就已经能够胜任 90% 的日常 Git 协同开发任务了。随着经验的积累，你可以再学习 </font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">rebase</font>`<font style="color:rgb(15, 17, 21);">、</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">stash</font>`<font style="color:rgb(15, 17, 21);"> 等更高级的工具来优化你的流程。</font>



提交过程  


1. <font style="color:rgb(15, 17, 21);">✅</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">已添加文件</font>**<font style="color:rgb(15, 17, 21);">：</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git add .</font>`
2. <font style="color:rgb(15, 17, 21);">✅</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">已提交更改</font>**<font style="color:rgb(15, 17, 21);">：</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git commit -m "message"</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);"></font>
3. <font style="color:rgb(15, 17, 21);">⏳</font><font style="color:rgb(15, 17, 21);"> </font>**<font style="color:rgb(15, 17, 21);">需要推送</font>**<font style="color:rgb(15, 17, 21);">：</font>`<font style="color:rgb(15, 17, 21);background-color:rgb(235, 238, 242);">git push origin main</font>`



