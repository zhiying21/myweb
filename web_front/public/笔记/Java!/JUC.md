多线程详细[https://liaoxuefeng.com/books/java/threading/basic/index.html](https://liaoxuefeng.com/books/java/threading/basic/index.html)

常用操作

| **<font style="color:rgb(17, 17, 51);">类别</font>** | **<font style="color:rgb(17, 17, 51);">操作 / 类 / 方法</font>** | **<font style="color:rgb(17, 17, 51);">功能说明</font>** | **<font style="color:rgb(17, 17, 51);">典型使用场景</font>** |
| --- | --- | --- | --- |
| **<font style="color:rgb(17, 17, 51);">线程创建</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">new Thread(Runnable).start()</font>`<br/><font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">建议写法:</font><font style="color:rgb(17, 17, 51);background-color:rgb(245, 247, 255);">Thread t = new Thread(new MyRunnable()); t.start(); // 启动新线程</font> | <font style="color:rgb(17, 17, 51);">创建并启动新线程</font> | <font style="color:rgb(17, 17, 51);">简单异步任务</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">implements Runnable</font>`<br/><font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">这个写法:往往是</font><br/>class MyRunnable implements Runnable {<br/>    @Override<br/>    public void run() {<br/>...<br/>    }<br/>}里面重写run方法去执行你要完成的任务 | <font style="color:rgb(17, 17, 51);">定义线程任务（推荐方式）</font> | <font style="color:rgb(17, 17, 51);">解耦任务与线程管理</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">extends Thread</font>` | <font style="color:rgb(17, 17, 51);">继承 Thread 类（不推荐）</font> | <font style="color:rgb(17, 17, 51);">仅用于简单 demo</font> |
| **<font style="color:rgb(17, 17, 51);">线程池</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Executors.newFixedThreadPool(n)</font>` | <font style="color:rgb(17, 17, 51);">固定大小线程池</font> | <font style="color:rgb(17, 17, 51);">控制并发数，如 Web 请求处理</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Executors.newCachedThreadPool()</font>` | <font style="color:rgb(17, 17, 51);">弹性线程池（空闲回收）</font> | <font style="color:rgb(17, 17, 51);">短时异步任务</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Executors.newSingleThreadExecutor()</font>` | <font style="color:rgb(17, 17, 51);">单线程顺序执行</font> | <font style="color:rgb(17, 17, 51);">保证任务串行执行（如日志写入）</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ThreadPoolExecutor</font>`<br/><font style="color:rgb(17, 17, 51);">（手动创建）</font> | <font style="color:rgb(17, 17, 51);">自定义线程池（生产推荐）</font> | <font style="color:rgb(17, 17, 51);">避免</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Executors</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">的资源耗尽风险</font> |
| **<font style="color:rgb(17, 17, 51);">同步控制</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">synchronized</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">关键字</font> | <font style="color:rgb(17, 17, 51);">内置锁，保证原子性</font> | <font style="color:rgb(17, 17, 51);">保护共享变量、方法</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ReentrantLock.lock()</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">.unlock()</font>` | <font style="color:rgb(17, 17, 51);">显式锁（更灵活）</font> | <font style="color:rgb(17, 17, 51);">需要超时、中断、公平锁等高级功能</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">volatile</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">变量</font> | <font style="color:rgb(17, 17, 51);">保证可见性 + 禁止指令重排序</font> | <font style="color:rgb(17, 17, 51);">状态标志（如</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">running = false</font>`<br/><font style="color:rgb(17, 17, 51);">）</font> |
| **<font style="color:rgb(17, 17, 51);">原子操作</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">AtomicInteger</font>`<br/><font style="color:rgb(17, 17, 51);">,</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">AtomicLong</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">等</font> | <font style="color:rgb(17, 17, 51);">无锁线程安全操作</font> | <font style="color:rgb(17, 17, 51);">计数器、序列号生成</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">AtomicReference</font>` | <font style="color:rgb(17, 17, 51);">原子引用更新</font> | <font style="color:rgb(17, 17, 51);">线程安全的对象替换</font> |
| **<font style="color:rgb(17, 17, 51);">线程协作</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">wait()</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">notify()</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">notifyAll()</font>` | <font style="color:rgb(17, 17, 51);">对象监视器等待/唤醒</font> | <font style="color:rgb(17, 17, 51);">生产者-消费者（配合 synchronized）</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Condition.await()</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">.signal()</font>` | <font style="color:rgb(17, 17, 51);">显式条件等待（配合 Lock）</font> | <font style="color:rgb(17, 17, 51);">更灵活的线程通信（如多个条件）</font> |
| **<font style="color:rgb(17, 17, 51);">同步工具类</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CountDownLatch</font>` | <font style="color:rgb(17, 17, 51);">等待 N 个任务完成</font> | <font style="color:rgb(17, 17, 51);">主线程等待多个初始化任务结束</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CyclicBarrier</font>` | <font style="color:rgb(17, 17, 51);">多线程互相等待到达屏障</font> | <font style="color:rgb(17, 17, 51);">并行计算后汇总结果</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Semaphore</font>` | <font style="color:rgb(17, 17, 51);">控制并发访问数量</font> | <font style="color:rgb(17, 17, 51);">限流、数据库连接池</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Phaser</font>` | <font style="color:rgb(17, 17, 51);">可重用、动态的 CyclicBarrier</font> | <font style="color:rgb(17, 17, 51);">多阶段任务协调</font> |
| **<font style="color:rgb(17, 17, 51);">并发集合</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ConcurrentHashMap</font>` | <font style="color:rgb(17, 17, 51);">线程安全的 HashMap</font> | <font style="color:rgb(17, 17, 51);">高并发缓存</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CopyOnWriteArrayList</font>` | <font style="color:rgb(17, 17, 51);">写时复制的 List</font> | <font style="color:rgb(17, 17, 51);">读多写少场景（如监听器列表）</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">BlockingQueue</font>`<br/><font style="color:rgb(17, 17, 51);">（如</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">LinkedBlockingQueue</font>`<br/><font style="color:rgb(17, 17, 51);">）</font> | <font style="color:rgb(17, 17, 51);">阻塞队列</font> | <font style="color:rgb(17, 17, 51);">生产者-消费者模型</font> |
| **<font style="color:rgb(17, 17, 51);">异步编程</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Future</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Callable</font>` | <font style="color:rgb(17, 17, 51);">获取异步任务结果</font> | <font style="color:rgb(17, 17, 51);">提交可返回结果的任务</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CompletableFuture</font>` | <font style="color:rgb(17, 17, 51);">链式异步 + 回调</font> | <font style="color:rgb(17, 17, 51);">异步编排、组合多个任务</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">parallelStream()</font>` | <font style="color:rgb(17, 17, 51);">并行流处理集合</font> | <font style="color:rgb(17, 17, 51);">快速并行计算（CPU 密集型）</font> |
| **<font style="color:rgb(17, 17, 51);">线程状态控制</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Thread.sleep(millis)</font>` | <font style="color:rgb(17, 17, 51);">当前线程休眠</font> | <font style="color:rgb(17, 17, 51);">模拟延迟、轮询间隔</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Thread.yield()</font>` | <font style="color:rgb(17, 17, 51);">让出 CPU（提示调度器）</font> | <font style="color:rgb(17, 17, 51);">很少用，效果不确定</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Thread.interrupt()</font>` | <font style="color:rgb(17, 17, 51);">中断线程</font> | <font style="color:rgb(17, 17, 51);">优雅终止阻塞线程,例如取消下载</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">isInterrupted()</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">interrupted()</font>` | <font style="color:rgb(17, 17, 51);">检查中断状态</font> | <font style="color:rgb(17, 17, 51);">响应中断信号，退出循环</font> |
| **<font style="color:rgb(17, 17, 51);">线程信息</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Thread.currentThread()</font>` | <font style="color:rgb(17, 17, 51);">获取当前线程对象</font> | <font style="color:rgb(17, 17, 51);">打印线程名、设置名称等</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Thread.setName()</font>` | <font style="color:rgb(17, 17, 51);">设置线程名称</font> | <font style="color:rgb(17, 17, 51);">调试时识别线程</font> |
| | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Thread.setDaemon(true)</font>` | <font style="color:rgb(17, 17, 51);">设为守护线程</font> | <font style="color:rgb(17, 17, 51);">后台任务（JVM 退出时不等待）</font> |


**<font style="color:rgb(17, 17, 51);">进程可以有多个线程,main是一个主线程,调用Thread ,并重写run来创建并运行新进程,本质是调用Thread里面的start(),start又调用里面的start0方法</font>**

<font style="color:rgb(51,51,51);">线程共享进程的</font>**<font style="color:rgb(51,51,51);">堆</font>**<font style="color:rgb(51,51,51);">和</font>**<font style="color:rgb(51,51,51);">方法区</font>**<font style="color:rgb(51,51,51);">资源 </font>

<font style="color:rgb(51,51,51);">每个线程有自己的</font>**<font style="color:rgb(51,51,51);">程序计数器</font>**<font style="color:rgb(51,51,51);">、</font>**<font style="color:rgb(51,51,51);">虚拟机栈</font>**<font style="color:rgb(51,51,51);">和</font>**<font style="color:rgb(51,51,51);">本地方法栈 </font>**

<font style="color:rgb(51,51,51);">线程切换的代码比进程小得多，线程也被称为轻量级进程</font>

```java
Thread t = new MyThread();
t.setDaemon(true);
t.start();
无论JVM退出时,不必关心守护线程是否已结束。
线程守护可以用在垃圾回收GC线程,缓存定时清理,日志异步写入,后台监控
```

在多进程中要以一组指令作为原子指令

```plain
┌───────┐     ┌───────┐
│Thread1│     │Thread2│
└───┬───┘     └───┬───┘
    │             │
    │-- lock --   │
    │ILOAD (100)  │
    │IADD         │
    │ISTORE (101) │
    │-- unlock -- │
    │             │-- lock --
    │             │ILOAD (101)
    │             │IADD
    │             │ISTORE (102)
    │             │-- unlock --
    ▼             ▼
```

## 线程同步的方式就是使用锁  

1,锁:在同一时间只有一个线程可以acquire锁的拥有权,直到release  
synchronized可以对某个函数对象,代码块上锁,这是一个可重入锁(<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">能在获取到锁以后继续获取同一个锁</font>),synchronized<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">每获取一次锁，记录+1，每退出</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">synchronized</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">块，记录-1，减到0的时候，才会真正释放锁。</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">按顺序获取锁就不会出现死锁</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">wait和notify锁的使用引入</font>

`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">synchronized</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">解决了多线程竞争的问题。例如，对于一个任务管理器，多个线程同时往队列中添加任务，可以用</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">synchronized</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">加锁：</font>

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">但是</font>`<font style="color:rgb(75, 85, 99);background-color:rgb(229, 231, 235);">synchronized</font>`<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">并没有解决多线程协调的问题。</font>

```java
class TaskQueue {
    Queue<String> queue = new LinkedList<>();
    public synchronized void addTask(String s) {
        this.queue.add(s);
    }
    public synchronized String getTask() {
        while (queue.isEmpty()) {
        }
        return queue.remove();
    }
}synchronized 修饰的实例方法，锁的是当前对象（this），也就是 TaskQueue 的实例。
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">  
</font><font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">使用wait()和notify()修改后的代码</font>

```plain
class TaskQueue {
    private final Queue<String> queue = new LinkedList<>();

    public synchronized void addTask(String s) {
        this.queue.add(s);
        this.notify(); // 唤醒一个等待的消费者
    }

    public synchronized String getTask() throws InterruptedException {
        while (queue.isEmpty()) {
            this.wait(); // ✅ 释放 this 锁，并进入等待状态
        }
        return queue.remove();
    }
}
```

synchronized会自己管理上锁,开锁,而ReentrantLock需要手动上锁解锁,但是生态更好,功能丰富,ReadWriteLock,有readLock()和writeLock(),分开写写两个方法,在有数据写入前都可以读,stampedLock使用乐观读的方式,性能更高,适用于高并发  


| <font style="color:rgb(17, 17, 51);">能力</font> | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">synchronized</font>` | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ReentrantLock</font>` | <font style="color:rgb(17, 17, 51);">价值</font> |
| --- | --- | --- | --- |
| **<font style="color:rgb(17, 17, 51);">可中断的锁等待</font>** | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 不支持</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">lockInterruptibly()</font>` | <font style="color:rgb(17, 17, 51);">避免线程永久阻塞</font> |
| **<font style="color:rgb(17, 17, 51);">超时获取锁</font>** | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 不支持</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">tryLock(timeout)</font>` | <font style="color:rgb(17, 17, 51);">防止死等，提升健壮性</font> |
| **<font style="color:rgb(17, 17, 51);">公平锁</font>** | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 非公平</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 可选公平策略</font> | <font style="color:rgb(17, 17, 51);">避免线程饥饿（如金融系统）</font> |
| **<font style="color:rgb(17, 17, 51);">锁的条件变量（Condition）</font>** | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 只有一个 wait-set</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 多个</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Condition</font>` | <font style="color:rgb(17, 17, 51);">实现复杂协作（如生产者-消费者多个队列）</font> |
| **<font style="color:rgb(17, 17, 51);">获取锁状态</font>** | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 无法查询</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">isLocked()</font>`<br/><font style="color:rgb(17, 17, 51);">,</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">getHoldCount()</font>` | <font style="color:rgb(17, 17, 51);">便于监控和调试</font> |


<font style="color:rgb(17, 17, 51);">其他常用锁</font>

### <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> Java 锁的种类与功能对照表</font>
| **<font style="color:rgb(17, 17, 51);">锁类型</font>** | **<font style="color:rgb(17, 17, 51);">所属包 / 关键字</font>** | **<font style="color:rgb(17, 17, 51);">核心功能</font>** | **<font style="color:rgb(17, 17, 51);">是否可重入</font>** | **<font style="color:rgb(17, 17, 51);">是否公平</font>** | **<font style="color:rgb(17, 17, 51);">是否可中断</font>** | **<font style="color:rgb(17, 17, 51);">典型使用场景</font>** |
| --- | --- | --- | --- | --- | --- | --- |
| **<font style="color:rgb(17, 17, 51);">synchronized</font>** | <font style="color:rgb(17, 17, 51);">语言关键字（JVM 内置）</font> | <font style="color:rgb(17, 17, 51);">自动加锁/释放，保证代码块或方法的原子性</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 否（非公平）</font> | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 否（不可中断）</font> | <font style="color:rgb(17, 17, 51);">简单同步、保护临界区</font> |
| **<font style="color:rgb(17, 17, 51);">ReentrantLock</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent.locks</font>` | <font style="color:rgb(17, 17, 51);">显式加锁/解锁，功能比</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">synchronized</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">更强大</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 可选（构造时指定）</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是（</font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">lockInterruptibly()</font>`<br/><font style="color:rgb(17, 17, 51);">）</font> | <font style="color:rgb(17, 17, 51);">需要超时、中断、公平锁等高级控制</font> |
| **<font style="color:rgb(17, 17, 51);"></font>** |  | <font style="color:rgb(17, 17, 51);"></font> | <font style="color:rgb(17, 17, 51);"></font> | <font style="color:rgb(17, 17, 51);"></font> | <font style="color:rgb(17, 17, 51);"></font> | <font style="color:rgb(17, 17, 51);"></font> |
| **<font style="color:rgb(17, 17, 51);">ReentrantReadWriteLock</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent.locks</font>` | <font style="color:rgb(17, 17, 51);">分离读锁（共享）和写锁（独占），提升读多写少场景性能</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 读/写锁各自可重入</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 可选</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">缓存、配置管理（读远多于写）</font> |
| **<font style="color:rgb(17, 17, 51);">StampedLock</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent.locks</font>`<br/><font style="color:rgb(17, 17, 51);">（JDK 8+）</font> | <font style="color:rgb(17, 17, 51);">支持</font>**<font style="color:rgb(17, 17, 51);">乐观读</font>**<font style="color:rgb(17, 17, 51);">（无锁读）、悲观读、写锁，性能更高</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);">（悲观模式）</font> | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 否</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">高并发读多写少，且能容忍短暂不一致（如统计）</font> |
| **<font style="color:rgb(17, 17, 51);">Semaphore（信号量）</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent</font>` | <font style="color:rgb(17, 17, 51);">控制同时访问某资源的线程数量（许可数）</font> | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 不是传统“锁”，但可做限流</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 可选</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">数据库连接池、限流、资源池</font> |
| **<font style="color:rgb(17, 17, 51);">CountDownLatch</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent</font>` | <font style="color:rgb(17, 17, 51);">允许一个或多个线程等待其他线程完成操作</font> | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 不是互斥锁</font> | <font style="color:rgb(17, 17, 51);">—</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是（等待可中断）</font> | <font style="color:rgb(17, 17, 51);">主线程等待多个初始化任务完成</font> |
| **<font style="color:rgb(17, 17, 51);">CyclicBarrier</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent</font>` | <font style="color:rgb(17, 17, 51);">多个线程互相等待到达屏障点后继续执行</font> | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 不是互斥锁</font> | <font style="color:rgb(17, 17, 51);">—</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">并行计算分阶段同步（如多线程模拟）</font> |
| **<font style="color:rgb(17, 17, 51);">Phaser</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent</font>` | <font style="color:rgb(17, 17, 51);">可重用、动态调整参与者的</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CyclicBarrier</font>` | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 不是互斥锁</font> | <font style="color:rgb(17, 17, 51);">—</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">多阶段任务协调（如 MapReduce）</font> |
| **<font style="color:rgb(17, 17, 51);">LockSupport</font>** | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">java.util.concurrent.locks</font>` | <font style="color:rgb(17, 17, 51);">底层线程阻塞/唤醒工具（</font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">park()</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">/</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">unpark()</font>`<br/><font style="color:rgb(17, 17, 51);">）</font> | <font style="color:rgb(17, 17, 51);">❌</font><font style="color:rgb(17, 17, 51);"> 工具类，非锁</font> | <font style="color:rgb(17, 17, 51);">—</font> | <font style="color:rgb(17, 17, 51);">✅</font><font style="color:rgb(17, 17, 51);"> 是</font> | <font style="color:rgb(17, 17, 51);">实现自定义同步器（如 AQS）</font> |


---

### <font style="color:rgb(17, 17, 51);">🔍</font><font style="color:rgb(17, 17, 51);"> 补充说明</font>
#### <font style="color:rgb(17, 17, 51);">1.</font><font style="color:rgb(17, 17, 51);"> </font>**<font style="color:rgb(17, 17, 51);">可重入（Reentrant）</font>**
+ <font style="color:rgb(17, 17, 51);">指同一个线程可以多次获取同一把锁而不死锁。</font>
+ `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">synchronized</font>`<font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">和</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ReentrantLock</font>`<font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">都支持，避免自己锁死自己。</font>

#### <font style="color:rgb(17, 17, 51);">2.</font><font style="color:rgb(17, 17, 51);"> </font>**<font style="color:rgb(17, 17, 51);">公平 vs 非公平</font>**
+ **<font style="color:rgb(17, 17, 51);">公平锁</font>**<font style="color:rgb(17, 17, 51);">：按请求顺序获取锁（FIFO），避免线程饥饿，但吞吐量低。</font>
+ **<font style="color:rgb(17, 17, 51);">非公平锁</font>**<font style="color:rgb(17, 17, 51);">：允许插队，吞吐量高，默认策略。</font>

#### <font style="color:rgb(17, 17, 51);">3.</font><font style="color:rgb(17, 17, 51);"> </font>**<font style="color:rgb(17, 17, 51);">乐观读（StampedLock）</font>**
+ <font style="color:rgb(17, 17, 51);">读时不加锁，通过 stamp 验证期间是否有写操作。</font>
+ <font style="color:rgb(17, 17, 51);">若验证失败，可升级为悲观读。</font>
+ **<font style="color:rgb(17, 17, 51);">注意</font>**<font style="color:rgb(17, 17, 51);">：不能保证强一致性，适用于容忍短暂不一致的场景。</font>

#### <font style="color:rgb(17, 17, 51);">4.</font><font style="color:rgb(17, 17, 51);"> </font>**<font style="color:rgb(17, 17, 51);">AQS（AbstractQueuedSynchronizer）</font>**
+ <font style="color:rgb(17, 17, 51);">上述</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ReentrantLock</font>`<font style="color:rgb(17, 17, 51);">、</font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Semaphore</font>`<font style="color:rgb(17, 17, 51);">、</font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CountDownLatch</font>`<font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">等底层都基于 AQS 实现。</font>
+ <font style="color:rgb(17, 17, 51);">是 Java 并发包的核心同步框架。</font>

---

### <font style="color:rgb(17, 17, 51);">📌</font><font style="color:rgb(17, 17, 51);"> 选择建议</font>
| <font style="color:rgb(17, 17, 51);">场景</font> | <font style="color:rgb(17, 17, 51);">推荐锁</font> |
| --- | --- |
| <font style="color:rgb(17, 17, 51);">简单同步，方法/代码块保护</font> | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">synchronized</font>`<br/><font style="color:rgb(17, 17, 51);">（简洁、自动释放）</font> |
| <font style="color:rgb(17, 17, 51);">需要超时、中断、公平性</font> | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ReentrantLock</font>` |
| <font style="color:rgb(17, 17, 51);">读多写少（如缓存）</font> | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ReentrantReadWriteLock</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">或</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">StampedLock</font>` |
| <font style="color:rgb(17, 17, 51);">控制并发数（如限流）</font> | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Semaphore</font>` |
| <font style="color:rgb(17, 17, 51);">等待 N 个任务完成</font> | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CountDownLatch</font>` |
| <font style="color:rgb(17, 17, 51);">多线程分阶段协作</font> | `<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">CyclicBarrier</font>`<br/><font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">或</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">Phaser</font>` |


<font style="color:rgba(17, 17, 51, 0.5);">💡</font><font style="color:rgba(17, 17, 51, 0.5);"> </font>**<font style="color:rgba(17, 17, 51, 0.5);">最佳实践</font>**<font style="color:rgba(17, 17, 51, 0.5);">：</font>

+ <font style="color:rgba(17, 17, 51, 0.5);">优先使用</font><font style="color:rgba(17, 17, 51, 0.5);"> </font>`<font style="color:rgba(17, 17, 51, 0.5);background-color:rgba(175, 184, 193, 0.2);">synchronized</font>`<font style="color:rgba(17, 17, 51, 0.5);">（JVM 优化好，不易出错）</font>
+ <font style="color:rgba(17, 17, 51, 0.5);">需要高级功能时再用</font><font style="color:rgba(17, 17, 51, 0.5);"> </font>`<font style="color:rgba(17, 17, 51, 0.5);background-color:rgba(175, 184, 193, 0.2);">ReentrantLock</font>`
+ <font style="color:rgba(17, 17, 51, 0.5);">高并发读场景考虑</font><font style="color:rgba(17, 17, 51, 0.5);"> </font>`<font style="color:rgba(17, 17, 51, 0.5);background-color:rgba(175, 184, 193, 0.2);">StampedLock</font>`<font style="color:rgba(17, 17, 51, 0.5);">（但注意其复杂性）</font>
+ <font style="color:rgba(17, 17, 51, 0.5);">避免手写 </font>`<font style="color:rgba(17, 17, 51, 0.5);background-color:rgba(175, 184, 193, 0.2);">wait/notify</font>`<font style="color:rgba(17, 17, 51, 0.5);">，优先用并发工具类</font>

## <font style="color:rgba(17, 17, 51, 0.5);">线程池</font>
<font style="color:rgb(17, 17, 51);">线程池（Thread Pool）是 Java 并发编程中一种</font>**<font style="color:rgb(17, 17, 51);">管理和复用线程的机制</font>**<font style="color:rgb(17, 17, 51);">，它的核心思想是：</font>**<font style="color:rgb(17, 17, 51);">预先创建一组线程，把任务提交给线程池，由池中的线程来执行任务，而不是为每个任务都创建新线程</font>**<font style="color:rgb(17, 17, 51);">。</font>

```plain
┌─────┐ execute  ┌──────────────────┐
│Task1│─────────▶│ThreadPool        │
├─────┤          │┌───────┐┌───────┐│
│Task2│          ││Thread1││Thread2││
├─────┤          │└───────┘└───────┘│
│Task3│          │┌───────┐┌───────┐│
├─────┤          ││Thread3││Thread4││
│Task4│          │└───────┘└───────┘│
├─────┤          └──────────────────┘
│Task5│
├─────┤
│Task6│
└─────┘
```

<font style="color:rgb(31, 41, 55);background-color:rgb(249, 250, 251);">简单地说，线程池内部维护了若干个线程，没有任务的时候，这些线程都处于等待状态。如果有新任务，就分配一个空闲线程执行。如果所有线程都处于忙碌状态，新任务要么放入队列等待，要么增加一个新线程进行处理。</font>

```java
// 创建固定大小的线程池://这种方式被阿里禁止了,因为无界队列,看下面
ExecutorService executor = Executors.newFixedThreadPool(3);
// 提交任务:
executor.submit(task1);
executor.submit(task2);
executor.submit(task3);
executor.submit(task4);
executor.submit(task5);
```

<font style="color:rgb(17, 17, 51);">线程池通过以下机制解决问题：</font>

| <font style="color:rgb(17, 17, 51);">问题</font> | <font style="color:rgb(17, 17, 51);">线程池的解决方案</font> |
| --- | --- |
| <font style="color:rgb(17, 17, 51);">资源耗尽</font> | **<font style="color:rgb(17, 17, 51);">限制最大线程数</font>**<font style="color:rgb(17, 17, 51);">（如最多 10 个线程），防止系统崩溃</font> |
| <font style="color:rgb(17, 17, 51);">生命周期开销</font> | **<font style="color:rgb(17, 17, 51);">复用已有线程</font>**<font style="color:rgb(17, 17, 51);">：任务执行完后，线程不销毁，而是回到池中等待新任务</font> |
| <font style="color:rgb(17, 17, 51);">缺乏管理</font> | <font style="color:rgb(17, 17, 51);">提供</font>**<font style="color:rgb(17, 17, 51);">任务队列、拒绝策略、统计监控、优雅关闭</font>**<font style="color:rgb(17, 17, 51);">等高级功能</font> |


## <font style="color:rgb(17, 17, 51);">线程池的核心组成（以 Java</font><font style="color:rgb(17, 17, 51);"> </font>`<font style="color:rgb(17, 17, 51);background-color:rgba(175, 184, 193, 0.2);">ThreadPoolExecutor</font>`<font style="color:rgb(17, 17, 51);"> </font><font style="color:rgb(17, 17, 51);">为例）</font>
```plain

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    2,                           // corePoolSize
    4,                           // maximumPoolSize
    60,                          // keepAliveTime
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100), // 有界队列！
    new ThreadFactory() {
        private int count = 0;
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "my-pool-" + count++);
        }
    },
    new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
);
```

### <font style="color:rgb(17, 17, 51);">提交任务：</font>
```plain
executor.submit(() -> {
    System.out.println("执行任务");
});
```

## ETC


