---

---

# TS简介

- TS是JS的超集

  ![屏幕截图 2025-03-24 174143](D:\Typora\AAAA_Note\我是前端\img\屏幕截图 2025-03-24 174143.png)

- TS=Type+JS，即**在JS的基础之上，为JS提供了类型支持**



- ~~~js
  // JS代码
  let age = 18
  
  // TS代码
  let age: number = 18
  ~~~



## 为什么加类型支持

- JS先天缺陷，JS绝大部分错误都是类型错误

- 语言动静态
  - TS 是静态类型编程语言，在编译期间做类型检查
  - JS 是动态类型编程语言，在执行期间做类型检查
  
- 代码先编译再执行，因此TS发现错误更早，更好维护

  ![屏幕截图 2025-03-24 173819](D:\Typora\AAAA_Note\我是前端\img\屏幕截图 2025-03-24 173819.png)



## TS开发环境搭配

由于浏览器和Node.js等都不认识TS，需要使用编译器把TS代码转换成JS代码再执行



- 导包

  - ~~~js
    npm i -g typescript
    ~~~

  - ~~~js
    // 检查包的版本
    tsc -v
    ~~~

    

- 创建.ts文件

- 使用tsc对ts文件进行编译

  - 进入文件所在终端（vscode右键文件即可，记事本cmd）

  - 执行命令：`tsc xxx.ts`，执行之后会在同目录下生成一个.js文件
  - 再执行JS代码：**在终端输入命令，`node xxx.js`**





**注意：**转换成的JS代码就没有类型了

![image-20250324211500103](D:\Typora\AAAA_Note\我是前端\img\image-20250324211500103.png)





## 便捷TS执行命令

- 导包

  - ~~~js
    npm i -g ts-node
    ~~~

  - 这个包提供`ts-node`命令，在内部自转换TS->JS（不会生成新的JS文件）

- 进入.ts文件终端，执行命令`ts-node xxx.ts`





## 错误总结

- 执行命令

  ```
  ts-node xxx.ts
  ```

  执行.ts文件时，发生如下**错误提示**

  ```
  TypeError: Unknown file extension ".ts" for......
  ```

  

  **此时只需要在.ts文件同目录下添加一个`tsconfig.json`文件，添加如下代码**

  ```
  {
    "ts-node": {
      "compilerOptions": {
        "module": "CommonJS"
      }
    }
  }
  ```

  即可





- 执行命令

  ```
  node xxx.ts
  ```

  执行.ts文件时，发生如下**错误提示**

  ```
  SyntaxError: Unexpected token ':'
  ```

  

  这是因为`node xxx.ts`是执行js文件的命令，不能识别ts的类型标识符号，还是推荐使用`ts-node xxx.ts`命令

  

  - 但是很神奇的一点，执行如下命令

    ```
    nodemon xxx.ts
    ```
  
    却可以执行.ts代码，也是很神奇了





- `nodemon`是基于`node.js`的一种更便携的命令

  - 导包

    ```
    npm i nodemon
    ```

  - 执行命令

    ```
    nodemon xxx.js
    ```

    

  - **一次执行，终身受益。**在执行命令后，每次保存代码，终端都会执行一次node命令，不用自己再一次一次执行命令，好用





# TS常用类型

- 分两类，JS已有类型和TS新增类型
  - JS已有类型
    - 原始类型：number/string/boolean/null/undefined/symbol
    - 对象类型：object（数组，对象，函数等对象）
  - TS新增对象
    - 联合类型/自定义类型/接口/元组/字面量类型/枚举/void/any等



## 原始类型

和JS书写名称相同，注意标识符相对应就行

~~~js
let age:number = 18
let uname:string = 'lmxgg'
~~~



## 数组类型

对象类型在TS中更加细化，每个具体的类型都有专属类型语法



- number[]写法**（推荐）**

  ```js
  let NUM: number[] = [1, 3, 5]
  ```



- **联合数组类型**（既有number类型，又有string类型）

  ```js
  let Arr: (number | string)[] = [1, 'a' ,3 , 'b']
  ```

  TS特有联合类型语法，联合多个类型



- 区别注意

  - 添加小括号，首先是数组，元素可以是number和string

    ```js
    let Arr: (number | string)[] = [1, 'a' ,3 , 'b']
    ```

    

  - 不添加小括号，可以是`number`类型，或者`string[]`数组类型

    ```js
    let Arr: number | string[] = 123
    
    let Arr: number | string[] = ['a', 'b']
    ```

  

  

## 自定义类型（类型别名）

- 也叫类型别名，可以为任意类型起别名

- 使用场景：当同一种类型需要多次被使用时，通过类型别名，**简化该种类型的使用**

  

- 使用步骤：
  - 使用`type`关键字来创建类型别名，类型别名可以是任意合法名称
  
  - 创建类型别名之后，**直接将其作为变量的类型注解**。（理解为代替number的使用）
  
    ```js
    type tmp = (number | string)[]
    let arr1:tmp = [1, 'a', 3, 'b']
    let arr2:tmp = ['x', 'y', 1, 2]
    ```





## 函数类型

### 基本类型

- 函数的类型就是函数的参数和返回值的类型

  - 单独指定参数、返回值的类型

    ```js
    function add(num1:number, num2:number):number {
    return num1+num2
    }
    ```

    

  - 同时指定参数、返回值的类型

    ```js
    const add: (num1:number, num2:number) => number = (num1,num2) => {
    return num1+num2
    }
    ```

    

### 可选参数

- 在函数实现某个功能时，**参数可传可不传，就可以使用到可选参数**

- 使用方法：在可选参数名称后面添加`？`

- 注意：可选参数只能出现在参数列表最后，**即可选参数之后不能再出现必选参数**

  ```js
  function f(A?: number, B?: number): void {
    console.log('起始索引是：', A, '结束索引是：', B);
  }
  
  f()
  f(1, 2)
  f(1)
  ```





## 对象类型

### 基本类型

- JS中对象由属性和方法构成，而TS中对象的属性就是在描述对象的结构

  ![image-20250326200958905](D:\Typora\AAAA_Note\我是前端\img\image-20250326200958905.png)

  ```js
  let person: { name: string; age: number; say(Hi: number): void } = {
    name: 'lmxgg',
    age: 20,
    say(Hi) {
      console.log(Hi);
    }
  }
  
  person.say(1)
  ```

  

### 可选属性

- 使用方法和可选参数一样

- 例如axios中发送GET请求时，method属性可以省略

  ```js
  function Axios(config: { url: string; method?: string }) {
    console.log(config)
  }
  
  Axios({
    url: 'https://www.baidu.com'
  })
  ```

  

### 接口（interface）

- 和自定义类型用处一样，达到代码复用的目的

  ```js
  interface Person { name: string; age: number; say(): void }
  
  let person: Person = {
    name: 'lmxgg',
    age: 20,
    say() {
      console.log('lmxgg');
    }
  }
  
  person.say()
  ```

  

#### 接口和类型别名的区别

- 接口只能为对象指定类型
- 类型别名可以为**任意对象指定别名**



#### 接口的继承

- 使用 `extends`关键字实现继承

  ```js
  interface father { x: number; y: number }
  interface son extends father { z: number }
  
  let son = {
    x: 1,
    y: 2,
    z: 3
  }
  
  console.log(son.x + son.y + son.z);
  ```



## 元组类型

- 一种特别的数组，可以**确切知道包含了哪些元素**，以及**特定索引对应的类型**

- 声明：

  ```ts
  let position:[number,string] = [1,'1']
  ```





## 字面量类型

- TS中任意的一个字符串，数字等都可以是一个类型

  ```ts
  let str1 = 'AAA'
  const str2 = 'AAA'
  ```

- 举例

  ```ts
  function change(f: 'up' | 'down' | 'left' | 'right') {
    console.log(f);
  }
  
  change('left')
  ```

  



## 枚举类型

- 枚举类型 = 字面量类型+联合类型

- 关键字：`enum`

  ```ts
  enum F { up, down, left, right }
  
  // 把F类型给到参数f
  function change(f: F) {
    console.log(f);
  }
  
  // 使用.语法访问枚举成员
  change(F.right)
  ```

  



## 类型推论

TS有类型推论机制，和JS不同的是TS会一直保持第一次推论的时候，JS会随时改变

```js
// 自动推论为number
let age = 18

// 自动推论返回值
function add(num1:number,num2:number):{return num1 + num2}
```





## 类型断言

- 在需要对某个类型进行操作时，若**该类型太宽泛**，**无法精确操作其下属的某个特有属性和方法**，就可以使用类型断言来**指定更加具体的类型**

- 举例：

  ```html
  <body>
      <a href="https://www.百度">
  </body>
  ```

  ```ts
  const mylink = document.getElementById('link')
  
  // 发生错误
  mylink.href
  ```

  - 此时mylink的类型是HTMLElement,但是mylink不能访问href属性
  - 因为HTMLElement只能访问共有的属性，**而a标签特有的href属性无法访问**



- 解决方法：使用 `as`关键字来指定更加具体的类型

  ```ts
  // HTMLAnchorElements是a标签的特有属性指代
  const mylink = document.getElementById('link') as HTMLAnchorElement
  
  // 不发生错误
  mylink.href
  ```

  

