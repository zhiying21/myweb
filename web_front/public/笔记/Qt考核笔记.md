#            Qt考核笔记

### 考核使用方法：

用户系统用结构体链表链接，其中包括了账号，密码借阅的书，和登入状态的信息；

书本用类表示，将书名，作者，数量，封装在父类中；

子类包含了书的类型为私有成员；父类中包含了各种信息的外部读写方法，在子类中用虚函数重写实现多态；

界面中修改了字体，按钮图标，添加了自定义的背景图；修改了图标，及其文档；主要使用了table Widget 和tab Widget来显示和完成各项操作

为了贴合窗口和背景的大小，限定了界面大小的放缩；

运用了QStringListModel模型和QAbstractItemModel；用于将数据一组一组的显示，捆绑后不容易出错；

靠ui->tableWidget->setItem()的循环显示数据

用户的借书和还书用qDebug()显示时间；

将必要的值在打开是初始化，关闭时销毁，如记录哲学类图书的种数的bookcount1实现多次登入，退出的数据稳定，同时将一些变量设为全局变量，避免退出再登入时数据消失，如用户借走的书保存在链表中，在打开界面的时候重新赋值到listView中；

listView使用了数据模型qlistmodel中的insert Row（qlistmodel->rowCount());在末尾插入一行

然后用`setData(index, value, role)` 将指定索引的数据设置为 `value`。将借的书插入到列表中

其中根据用户的登入状态太将借走的书存到用户链表中；

在管理员系统中使用了QSQLite数据库包括安装驱动，设置路径名和打开数据库；

使用数据库创建了一个table Widget 包含了四种数据；使用拼接将四种数据连起来得到插入语句来完成数据的插入，实现数据的添加，然后根据类里的get_函数对类成员数据改变；

根据定义的数据库成员.value(i).toString()的方法获取表中的值，.next()作为循环条件完成显示的功能，

使用qlistmodel模型，完成管理员的用户系统的查询功能中显示该用户借书的功能

在将变量使用范围拓宽到其他文件时使用了extern放在头文件中



### 设计逻辑：

注册和登入功能：将用户键入的账号密码先检测有没有重复的账号，然后放入字符串数组中；然后将注册成功的数据由数组的值传到链表用户中，在用户登入时也是通过数组作为媒介检查用户键入的账号密码是否正确，若是输入管理员的账号和密码可直接登入；

数据储存：将书记有关数据存到类中，用户有关数据存到链表中，这是全局变量，不会因为反复的登入退出而重置，每注册成功一个成员，链表的长度就会加1；自定义数据结构和表格之间用QString 数组过渡；

界面显示：管理员系统和用户系统使用tab Widget为功能选择框，tableWidget显示多组数据，ListView显示多个数据；

用户链表：通过登入开关对指定用户节点进行数据操作，其中运用了多种函数，如开关转换器，尾插法加入节点，获取链表长度，和寻找登入用户所在节点；

## 学习笔记

##### 数据库

qmake文件里加QT+=sql；

CMake文件加

find_package(Qt6 REQUIRED COMPONENTS Sql)

target_link_libraries(book PRIVATE Qt6::Sql)

.h文件加私有成员QSqlDatabase base;

//安装数据库的驱动
    base = QSqlDatabase::addDatabase("QSQLITE");

    //设置数据库的路径名
    base.setDatabaseName("D:/Qtsql/1.db");
    
    //打开数据库
    bool ret = base.open();
    if(!ret)
    {
        qDebug()<<"打开数据库失败了,失败的原因"<<base.lastError().text();
        return;
    }
    
    //新建一个表格
    QSqlQuery query(base);

.exec用于执行SQL语句，返回一个bool值，可以用于判断是否执行成功

 query.exec("drop table if exists booktable;"); // 删除已存在的表
    ret = query.exec("create table booktable (book_name text, writer text, sort text, leftcount int);");//四个项目
    if(!ret)
    {
        qDebug()<<"新建书籍表格失败了,失败的原因"<<query.lastError().text();
        return;
    }

##### 数据处理

 QString book_name = ui->add_book->text();
    QString writer = ui->add_writer->text();
    QString sort = ui->sort->text();
    QString leftcount = ui->leftcount->text();
    //拼接得到插入语句
    QString cmd = QString("insert into booktable values('%1','%2','%3',%4);").arg(book_name).arg(writer).arg(sort).arg(leftcount);

使用全局变量时，在cpp定义，.h加extern，其他文件包含头文件就可以直接使用

// bookname1.append(bookname2);
    // writer1.append(writer2);//.append()可以用于链接字符串

QStringList可以用<<将QString加在末位

#### QStringListModel

.h文件包含

#include<QtSql/QSqlDatabase>
#include<QtSql/QSqlQuery>
#include<QtSql/QSqlError>

添加私有成员

     QStringListModel *qlistmodel = NULL;//数据模型的对象指



#### table Widget

 // QSqlQuery query (base);int i = 0,j;
    // bool ret = query.exec("select * from booktable;");
    // if(!ret)
    // {
    //     qDebug()<<"查询书籍信息失败了,失败的原因"<<query.lastError().text();
    //     return;
    // }//QVariant类是万能类型
    // //value:通过字段索引号，得到字段信息
    // // next方法:如果查询结果遍历完毕，返回假
    // //获取查询结果
    // while (query.next()) {
    //     for( j = 0;j<bookcount1;j++)
    //     {
    //         QString str = query.value(j).toString();
    //         QTableWidgetItem *Item = new QTableWidgetItem(str);
    //         ui->tableWidget->setItem(i,j,Item);
    //     }
    //     i++;
    // }

也可以用

int row,i = 0;
    for(row = 0;row<bookcount1;row++)
    {
        int col = 0;
        ui->tableWidget->setItem(row,col++,new QTableWidgetItem(bookname1[row]));
        ui->tableWidget->setItem(row,col++,new QTableWidgetItem(writer1[row]));
        ui->tableWidget->setItem(row,col++,new QTableWidgetItem(getsort(&book1[0])));
        ui->tableWidget->setItem(row,col,new QTableWidgetItem(QString::number(book1[row].display_leftCount())));
    }

    for(;row<bookcount2+bookcount1;row++)
    {
        int col = 0;
        ui->tableWidget->setItem(row,col++,new QTableWidgetItem(bookname2[row-bookcount1]));
        ui->tableWidget->setItem(row,col++,new QTableWidgetItem(writer2[row-bookcount1]));
        ui->tableWidget->setItem(row,col++,new QTableWidgetItem(getsort(&book2[0])));
        ui->tableWidget->setItem(row,col,new QTableWidgetItem(QString::number(book2[row-bookcount1].display_leftCount())));
    
    }



##### 时间

void getCurrentTime() {
    time_t current_time;  // 定义一个 time_t 类型的变量
    current_time = time(NULL);  // 获取当前时间
    const char *time_str = std::ctime(&current_time);  // 使用 std::ctime 获取时间字符串

    // 打印时间字符串
    qDebug() << "Current time:" << time_str;
}

##### 类

class booksort
{
private:
    int leftCount = 5;
    QString bookname;
    QString writer;//封装
public:
    //Book(const QString bookname,const QString& writer);
    void get_writer(QString *s)
    {
        writer = *s;
    }
    QString set_writer()//提供外部读取办法
    {
        return writer;
    }
    void get_bookname(QString *t)
    {
        bookname = *t;
    }
    QString set_bookname()
    {
        return bookname;
    }
    void borrow()
    {
        if(leftCount==0)
            qDebug()<<"无剩余图书"<<Qt::endl;
        else{
            leftCount--;
            qDebug()<<"借书成功";
        }
    }
    void giveback()
    {
        leftCount++;
        qDebug()<<"还书成功";
    }
    void get_leftcount(int t)
    {
        leftCount = t;
    }
    int display_leftCount()
    {
        return leftCount;
    }

    virtual QString get_sort() = 0;//虚函数待重写
};


class philosophy:public booksort{//使用继承，继承了父类元素
private:
    QString sort = "哲学类";//封装图书的类型

public:
    QString get_sort(){//提供外部读写办法
        return sort;
    }
};
class socialscience:public booksort
{
private:
    QString sort = "社会科学类";//封装图书的类型

public:
    QString get_sort(){//提供外部读写办法
        return sort;
    }
};

QString getsort(booksort* ptr)//使用多态,显示图书分类情况
{
    return ptr->get_sort();
}

##### 结构体

typedef struct User{//定义结构体类型的链表
    QString account_sets,password_sets;int dex = 0;//分别是账号，密码，登入开关
    QString borrow_book[20];
    struct User *next;
}user;

user * createlist(int n)//创建链表
{
    user *head = new user;
    user *pre = head;
    for(int i = 0;i<n;i++)
    {
        user *p = new user;
        pre->next = p;
        pre = p;
        p->next = NULL;
    }return head;
}


int length(user * head)//获取链表长度
{

    int count = 0;
    user *p = head->next;
    while(p!=NULL)
    {
        count++;
        p = p->next;
    }
    return count;
}

void insert_word_Element(user *head,int index)//用来插入链表(注册时使用记录)
{

    user *pre = head;
    for(int i = 0;i<index;i++)//循环找到链表位置
    {
        pre = pre->next;
    }
    user * newNode = new user;
    newNode->account_sets = account[index];//将账号密码存到链表中
    newNode->password_sets =password[index];
    
    newNode->next = pre->next;
    pre->next = newNode;
}
void Login_switch(user *head,int index){//登入退出时使用的开关，用于后续找到账号的登入情况


    user *pre = head->next;
    for(int i = 0;i<index;i++)
    {
        pre = pre->next;
    }
    if(pre->dex == 0)
        pre->dex = 1;
    else pre->dex = 0;
}
int find_dex(user *head){//用于找到是哪个账号处于登入状态，返回节点位置
    user *pre = head->next;
    int i;
    for(i = 1;i<=length(head);i++)
    {
        if(pre->dex == 1)
        {return i;}
        else pre = pre->next;
    }
    return i;
}
/*int check(int account)//检查用户名，确保用户唯一性
{
    for(int i = 0;i<=n;i++)
    {
        if(account == account_set[i])
        {
            cout<<"该用户已经存在，请重新输入"<<endl;
            return 1;
        }
    }return 0;
}*/

