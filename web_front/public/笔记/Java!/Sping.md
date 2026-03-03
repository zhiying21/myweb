## Maven
<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">在了解Maven之前，我们先来看看一个Java项目需要的东西。首先，我们需要确定引入哪些依赖包。例如，如果我们需要用到</font>[<font style="color:rgb(2, 132, 199);background-color:rgb(249, 250, 251);">commons logging</font>](https://commons.apache.org/proper/commons-logging/)<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，我们就必须把commons logging的jar包放入classpath。如果我们还需要</font>[<font style="color:rgb(2, 132, 199);background-color:rgb(249, 250, 251);">log4j</font>](https://logging.apache.org/log4j/)<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，就需要把log4j相关的jar包都放到classpath中。这些就是依赖包的管理。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">其次，我们要确定项目的目录结构。例如，</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">src</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">目录存放Java源码，</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">resources</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">目录存放配置文件，</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">bin</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">目录存放编译生成的</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">.class</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">文件。</font>

### <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">Maven项目结构</font>
<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">一个使用Maven管理的普通的Java项目，它的目录结构默认如下：</font>

```plain
a-maven-project
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   └── resources
│   └── test
│       ├── java
│       └── resources
└── target
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">我们再来看最关键的一个项目描述文件</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">pom.xml</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，它的内容长得像下面：</font>

```xml
<project ...>
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.itranswarp.learnjava</groupId>
	<artifactId>hello</artifactId>
	<version>1.0</version>
	<packaging>jar</packaging>
	<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<maven.compiler.release>17</maven.compiler.release>
	</properties>
	<dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.16</version>
        </dependency>
	</dependencies>
</project>
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">其中，</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">groupId</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">类似于Java的包名，通常是公司或组织名称，</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">artifactId</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">类似于Java的类名，通常是项目名称，再加上</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">version</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，一个Maven工程就是由</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">groupId</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">artifactId</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">和</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">version</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">作为唯一标识。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">我们在引用其他第三方库的时候，也是通过这3个变量确定。例如，依赖</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">org.slfj4:slf4j-simple:2.0.16</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">：</font>

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.16</version>
</dependency>
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);"></font>

**<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">依赖管理问题引入:</font>**

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">如果我们的项目依赖第三方的jar包，例如commons logging，那么问题来了：commons logging发布的jar包在哪下载？</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">如果我们还希望依赖log4j，那么使用log4j需要哪些jar包？</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">类似的依赖还包括：JUnit，JavaMail，MySQL驱动等等，一个可行的方法是通过搜索引擎搜索到项目的官网，然后手动下载zip包，解压，放入classpath。但是，这个过程非常繁琐。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">Maven解决了依赖管理问题。例如，我们的项目依赖</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">这个jar包，而</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">又依赖</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">xyz</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">这个jar包：</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">当我们声明了</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">的依赖时，Maven自动把</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">和</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">xyz</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">都加入了我们的项目依赖，不需要我们自己去研究</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">是否需要依赖</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">xyz</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">因此，Maven的第一个作用就是解决依赖管理。我们声明了自己的项目需要</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，Maven会自动导入</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">的jar包，再判断出</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">需要</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">xyz</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，又会自动导入</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">xyz</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">的jar包，这样，最终我们的项目会依赖</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">abc</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">和</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">xyz</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">两个jar包。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">如</font><font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">我们来看一个复杂依赖示例：</font>

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>1.4.2.RELEASE</version>
</dependency>
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">当我们声明一个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">spring-boot-starter-web</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">依赖时，Maven会自动解析并判断最终需要大概二三十个其他依赖</font>

**<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">依赖关系</font>**

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">Maven定义了几种依赖关系，分别是</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">compile</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">、</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">test</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">、</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">runtime</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">和</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">provided</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">：</font>

| <font style="color:rgb(31, 41, 55);">scope</font> | <font style="color:rgb(31, 41, 55);">说明</font> | <font style="color:rgb(31, 41, 55);">示例</font> |
| :--- | :--- | :--- |
| <font style="color:rgb(31, 41, 55);">compile</font> | <font style="color:rgb(31, 41, 55);">编译时需要用到该jar包（默认）</font> | <font style="color:rgb(31, 41, 55);">commons-logging</font> |
| <font style="color:rgb(31, 41, 55);">test</font> | <font style="color:rgb(31, 41, 55);">编译Test时需要用到该jar包</font> | <font style="color:rgb(31, 41, 55);">junit</font> |
| <font style="color:rgb(31, 41, 55);">runtime</font> | <font style="color:rgb(31, 41, 55);">编译时不需要，但运行时需要用到</font> | <font style="color:rgb(31, 41, 55);">mysql</font> |
| <font style="color:rgb(31, 41, 55);">provided</font> | <font style="color:rgb(31, 41, 55);">编译时需要用到，但运行时由JDK或某个服务器提供</font> | <font style="color:rgb(31, 41, 55);">servlet-api</font> |


如

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.3.2</version>
    <scope>test</scope>
</dependency>
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">最后一个问题是，Maven如何知道从何处下载所需的依赖？也就是相关的jar包？答案是Maven维护了一个中央仓库（</font>[<font style="color:rgb(2, 132, 199);">repo1.maven.org</font>](https://repo1.maven.org/)<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">），所有第三方库将自身的jar以及相关信息上传至中央仓库，Maven就可以从中央仓库把所需依赖下载到本地。</font>

### 构建流程
生命周期:它由很多个阶段构成

默认周期

+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">validate</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">initialize</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">generate-sources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">process-sources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">generate-resources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">process-resources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">compile</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">process-classes</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">generate-test-sources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">process-test-sources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">generate-test-resources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">process-test-resources</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">test-compile</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">process-test-classes</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">test</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">prepare-package</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">package</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">pre-integration-test</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">integration-test</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">post-integration-test</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">verify</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">install</font>
+ <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">deploy</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">如果我们运行</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">mvn package</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，Maven就会执行</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">default</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">生命周期，它会从开始一直运行到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">package</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">这个phase为止：</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">如果我们运行</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">mvn compile</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，Maven也会执行</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">default</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">生命周期，但这次它只会运行到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">compile</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，即以下几个phase：</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">在实际开发过程中，经常使用的命令有：</font>

`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">mvn clean</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">：清理所有生成的class和jar；</font>

`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">mvn clean compile</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">：先清理，再执行到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">compile</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">；</font>

`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">mvn clean test</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">：先清理，再执行到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">test</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，因为执行</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">test</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">前必须执行</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">compile</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，所以这里不必指定</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">compile</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">；</font>

`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">mvn clean package</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">：先清理，再执行到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">package</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">maven是通过插件执行命令的,我们可以自定义插件.例如，使用</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">maven-shade-plugin</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">可以创建一个可执行的jar，要使用这个插件，需要在</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">pom.xml</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">中声明它：</font>

### <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">模块管理</font>
```plain
multiple-project
├── pom.xml
├── parent
│   └── pom.xml
├── module-a
│   ├── pom.xml
│   └── src
├── module-b
│   ├── pom.xml
│   └── src
└── module-c
    ├── pom.xml
    └── src
```

每个module是独立的Maven项目,<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">注意到parent的</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);"><packaging></font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">是</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">pom</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">而不是</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">jar</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，因为</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">parent</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">本身不含任何Java代码。编写</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">parent</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">的</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">pom.xml</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">只是为了在各个模块中减少重复的配置。  
</font><font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">模块A,模块B、模块C都可以直接从</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">parent</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">继承，大幅简化了</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">pom.xml</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">的编写。</font>

最后，在编译的时候，需要在根目录创建一个pom.xml统一编译：



```plain
<modelVersion>4.0.0</modelVersion>
<groupId>com.itranswarp.learnjava</groupId>
<artifactId>build</artifactId>
<version>1.0</version>
<packaging>pom</packaging>
<name>build</name>
<modules>
    <module>parent</module>
    <module>module-a</module>
    <module>module-b</module>
    <module>module-c</module>
</modules>
```

mvnw可以指定特定的Maveb版本

## <font style="color:rgb(17, 17, 51);">🆚</font><font style="color:rgb(17, 17, 51);"> 对比：Spring Boot 项目 vs 普通 Maven 项目</font>
| <font style="color:rgb(17, 17, 51);">项目类型</font> | <font style="color:rgb(17, 17, 51);">生成方式</font> | <font style="color:rgb(17, 17, 51);">适用场景</font> |
| --- | --- | --- |
| **<font style="color:rgb(17, 17, 51);">普通 Java 项目</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">mvn archetype:generate</font>` | <font style="color:rgb(17, 17, 51);">算法、工具类、学习 Java 基础</font> |
| **<font style="color:rgb(17, 17, 51);">Spring Boot 项目</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">curl https://start.spring.io</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">或 IDEA</font> | <font style="color:rgb(17, 17, 51);">Web 后端、微服务、实习/正式项目</font> |


## Ioc
Ioc是控制反转,使用spring容器控制,可以解决紧耦合,难维护的问题

<font style="color:rgb(17, 17, 51);">Spring 的 </font>**<font style="color:rgb(17, 17, 51);">IoC（Inversion of Control，控制反转）容器</font>**<font style="color:rgb(17, 17, 51);"> 是 Spring 框架的核心，它通过一系列机制实现了 </font>**<font style="color:rgb(17, 17, 51);">对象的创建、装配、生命周期管理与解耦</font>**<font style="color:rgb(17, 17, 51);">，从而让开发者从繁琐的对象管理和依赖关系中解放出来。</font>

**<font style="color:rgba(17, 17, 51, 0.5);">Spring IoC 容器 = 一个高度自动化的“对象工厂 + 装配车间”</font>**

| <font style="color:rgb(17, 17, 51);">传统开发</font> | <font style="color:rgb(17, 17, 51);">使用 Spring IoC</font> |
| --- | --- |
| <font style="color:rgb(17, 17, 51);">手动</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">new</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">对象</font> | <font style="color:rgb(17, 17, 51);">容器自动创建</font> |
| <font style="color:rgb(17, 17, 51);">硬编码依赖（</font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">new UserRepository()</font>`<br/><font style="color:rgb(17, 17, 51);">）</font> | <font style="color:rgb(17, 17, 51);">声明依赖，容器自动注入</font> |
| <font style="color:rgb(17, 17, 51);">自己管理对象生命周期</font> | <font style="color:rgb(17, 17, 51);">容器统一管理</font> |
| <font style="color:rgb(17, 17, 51);">配置写死在代码里</font> | <font style="color:rgb(17, 17, 51);">配置外部化，运行时注入</font> |


  
问题引入:在电商后端BookService,UserService,CartServlet等服务中  
都需要实例化一个对象,每项服务要读先取配置,,访问数据库再实例化,我们完全可以<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">完全可以共享同一个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">DataSource</font>`

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">因此，核心问题是：</font>

1. <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">谁负责创建组件？</font>
2. <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">谁负责根据依赖关系组装组件？</font>
3. <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">销毁时，如何按依赖顺序正确销毁？</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">解决这一问题的核心方案就是IoC。</font>

### <font style="color:rgb(17, 17, 51);"></font><font style="color:rgb(17, 17, 51);">IoC 怎么解决？（核心机制）</font>
<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">现在Ioc主要通过DI依赖注入实现</font><font style="color:rgb(17, 17, 51);">  
</font><font style="color:rgb(17, 17, 51);">Spring 通过 </font>**<font style="color:rgb(17, 17, 51);">IoC 容器（BeanFactory / ApplicationContext）</font>**<font style="color:rgb(17, 17, 51);"> 来解决：</font>

<font style="color:rgb(17, 17, 51);">步骤 1：</font>**<font style="color:rgb(17, 17, 51);">你只声明“需要什么”</font>**

```java
@Service
public class OrderService {
    @Autowired  // ← 告诉 Spring：“我需要一个 SmsService”
    private SmsService smsService;

    public void createOrder() {
        smsService.send("下单成功");
    }
}
```

```java
public class BookService {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">不直接</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">new</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">一个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">DataSource</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，而是注入一个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">DataSource</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，这个小小的改动虽然简单，却带来了一系列好处：</font>

1. `<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">BookService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">不再关心如何创建</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">DataSource</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，因此，不必编写读取数据库配置之类的代码；</font>
2. `<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">DataSource</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">实例被注入到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">BookService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，同样也可以注入到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">UserService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，因此，共享一个组件非常简单；</font>
3. <font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">测试</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">BookService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">更容易，因为注入的是</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">DataSource</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，可以使用内存数据库，而不是真实的MySQL配置。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">因此，IoC又称为依赖注入（DI：Dependency Injection），它解决了一个最主要的问题：将组件的创建+配置与组件的使用相分离，并且，由IoC容器负责管理组件的生命周期。</font>

**<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">但是依赖注入是IOC容器实现控制反转的一种方式</font>**

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">DI的三种注入方式</font>

1. **<font style="color:rgb(17, 17, 51);">构造器注入（推荐）</font>**

```plain
@Service
public class OrderService {
    private final SmsService smsService;
    public OrderService(SmsService smsService) {
        this.smsService = smsService; // Spring 自动传入
    }
}
```

<font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 优点：不可变、保证非空、利于单元测试</font>

2. **<font style="color:rgb(17, 17, 51);">Setter 注入</font>**
3. **<font style="color:rgb(17, 17, 51);">字段注入（@Autowired，不推荐）</font>**

### **<font style="color:rgba(17, 17, 51, 0.5);">装配Bean</font>**
1.通过@Component和@Service注解定义Bean  
2.通过配置文件定义Bean

3.定义好Bean后可以通过@Autowired或者@Resourse注解执行依赖注入,去使用IOC里面的Bean对象

   


| <font style="color:rgb(17, 17, 51);">注解</font> | <font style="color:rgb(17, 17, 51);">用途</font> | <font style="color:rgb(17, 17, 51);">适用层级</font> | <font style="color:rgb(17, 17, 51);">说明</font> |
| --- | --- | --- | --- |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@Component</font>` | **<font style="color:rgb(17, 17, 51);">通用组件</font>** | <font style="color:rgb(17, 17, 51);">任何自定义组件</font> | <font style="color:rgb(17, 17, 51);">最基础的注解，适用于不属于特定层的工具类、配置类等</font> |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@Service</font>` | **<font style="color:rgb(17, 17, 51);">业务逻辑层</font>** | <font style="color:rgb(17, 17, 51);">Service 层</font> | <font style="color:rgb(17, 17, 51);">语义明确表示“这是业务服务”，便于理解和维护</font> |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@Repository</font>` | **<font style="color:rgb(17, 17, 51);">数据访问层</font>** | <font style="color:rgb(17, 17, 51);">DAO / Repository 层</font> | <font style="color:rgb(17, 17, 51);">额外提供</font><font style="color:rgb(17, 17, 51);"> </font>**<font style="color:rgb(17, 17, 51);">数据库异常自动转换</font>**<font style="color:rgb(17, 17, 51);">（如 SQLException → DataAccessException）</font> |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@Controller</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@RestController</font>` | **<font style="color:rgb(17, 17, 51);">Web 控制层</font>** | <font style="color:rgb(17, 17, 51);">Controller 层</font> | <font style="color:rgb(17, 17, 51);">标识 Web 请求入口</font> |


<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">注意到</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">UserService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">通过</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">setMailService()</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">注入了一个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">MailService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">然后，我们需要编写一个特定的</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">application.xml</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">配置文件，告诉Spring的IoC容器应该如何创建并组装Bean：</font>

```xml
<bean id="userService" class="com.itranswarp.learnjava.service.UserService">
        <property name="mailService" ref="mailService" />
    </bean>

    <bean id="mailService" class="com.itranswarp.learnjava.service.MailService" />
</beans>
```



### Annotation
<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">使用XML配置的优点是所有的Bean都能一目了然地列出来，并通过配置注入能直观地看到每个Bean的依赖。它的缺点是写起来非常繁琐，每增加一个组件，就必须把新的Bean配置到XML中。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">有没有其他更简单的配置方式呢？</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">有！我们可以使用Annotation配置，可以完全不需要XML，让Spring自动扫描Bean并组装它们。</font>

```java
@Component
public class MailService {
    ...
}
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">这个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">@Component</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">注解就相当于定义了一个Bean，它有一个可选的名称，默认是</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">mailService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">，即小写开头的类名。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">然后，我们给</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">UserService</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">添加一个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">@Component</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">注解和一个</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">@Autowired</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">注解：</font>

```java
@Component
public class UserService {
    @Autowired
    MailService mailService;

    ...
}
```

### Resource
<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">在Java程序中，我们经常会读取配置文件、资源文件等。使用Spring容器时，我们也可以把“文件”注入进来，方便程序读取。</font>

```java
@Value("classpath:/logo.txt")
    private Resource resource;
```

## AOP
**<font style="color:rgba(17, 17, 51, 0.5);">“在不修改原有业务代码的前提下，统一给多个方法‘织入’横切逻辑。”</font>**

## <font style="color:rgb(17, 17, 51);">❓</font><font style="color:rgb(17, 17, 51);"> 二、AOP 解决什么问题？</font>


| <font style="color:rgb(17, 17, 51);">问题</font> | <font style="color:rgb(17, 17, 51);">AOP 如何解决</font> |
| --- | --- |
| **<font style="color:rgb(17, 17, 51);">代码重复</font>** | <font style="color:rgb(17, 17, 51);">日志、监控、事务等逻辑集中管理</font> |
| **<font style="color:rgb(17, 17, 51);">侵入性强</font>** | <font style="color:rgb(17, 17, 51);">业务代码无需知道 AOP 的存在</font> |
| **<font style="color:rgb(17, 17, 51);">难以维护</font>** | <font style="color:rgb(17, 17, 51);">修改日志格式？只需改一个切面类！</font> |
| **<font style="color:rgb(17, 17, 51);">违反单一职责</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">OrderService</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">只负责订单，不负责日志</font> |


1️⃣ 核心术语（面试必问！）  
术语	说明	类比  
Aspect（切面）	横切逻辑的模块化（如日志切面）	“功能插件”  
Join Point（连接点）	程序执行过程中的某个点（如方法调用）	“可插入的钩子”  
Pointcut（切入点）	匹配哪些 Join Point 要被拦截（如 execution(* com.service._._(..))）	“匹配规则”  
Advice（通知）	在 Join Point 执行的代码（如 @Before, @After, @Around）	“具体要做的事”  
Weaving（织入）	将 Aspect 应用到目标对象的过程（Spring 用动态代理实现）	“自动插入”

```java
@Aspect
@Component
public class LoggingAspect {
    // 在执行UserService的每个方法前执行:
    @Before("execution(public * com.itranswarp.learnjava.service.UserService.*(..))")
    public void doAccessCheck() {
        System.err.println("[Before] do access check...");
    }
```

### <font style="color:rgb(17, 17, 51);">2️⃣</font><font style="color:rgb(17, 17, 51);"> </font>**<font style="color:rgb(17, 17, 51);">Advice 类型（5 种）</font>**
| <font style="color:rgb(17, 17, 51);">注解</font> | <font style="color:rgb(17, 17, 51);">时机</font> | <font style="color:rgb(17, 17, 51);">典型用途</font> |
| --- | --- | --- |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@Before</font>` | <font style="color:rgb(17, 17, 51);">方法执行前</font> | <font style="color:rgb(17, 17, 51);">权限校验、参数校验</font> |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@After</font>` | <font style="color:rgb(17, 17, 51);">方法执行后（无论是否异常）</font> | <font style="color:rgb(17, 17, 51);">资源清理</font> |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@AfterReturning</font>` | <font style="color:rgb(17, 17, 51);">方法成功返回后</font> | <font style="color:rgb(17, 17, 51);">日志记录、结果缓存</font> |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@AfterThrowing</font>` | <font style="color:rgb(17, 17, 51);">方法抛出异常后</font> | <font style="color:rgb(17, 17, 51);">异常监控、告警</font> |
| `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">@Around</font>` | **<font style="color:rgb(17, 17, 51);">环绕整个方法</font>**<font style="color:rgb(17, 17, 51);">（最强大！）</font> | <font style="color:rgb(17, 17, 51);">性能监控、事务管理</font> |


