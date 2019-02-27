# EasyMySQL
## 一个简化的 mysql 数据库操作库。。。                                                                                     
## A simplified toolkit for mysql...

[gitee 仓库](https://gitee.com/FishGoddess/EasyMySQL)


**最终可用版本在 releases 文件夹中**<br/>
the final version is in releases<br/>

**项目发布版本分为两种，一种是不带依赖的 jar 包，占用空间很小；一种是带了全部依赖的 jar 包，方便引用。**<br/>
it has two releases version: one is jar with no dependency, the other one is jar with dependencies<br/>

### 下面演示了结合 AutoMySQL 小框架的查询操作：
#### if you use AutoMySQL and this jar:
#### AutoMySQL 请点击: [AutoMySQL](https://github.com/FishGoddess/AutoMySQL)
##### （代码中的 com.fish.Book.java 在源码中有提供）
```java
import com.fish.core.DBManager;
import com.fish.core.DBWorker;
import java.io.File;
    
public class Test
{ 
    public static void main(String[] args)
    {
        // set config...
        DBManager.init(new File("DB.properties"));
    
        // auto commit ==> default is false
        //DBManager.setAutoCommit(false);
    
        // connect to database...
        DBWorker dbWorker = DBManager.getDBWorker();
    
        com.fish.Book book = new com.fish.Book();
        book.setName("奇异人生");
        book.setPrice(68);
    
        // insert a new book in database...
        //dbWorker.insert(book);
    
        // put whole table in a file...
        //dbWorker.putTableInFile("book", "Z:/book.txt");
    
        // query a book...
        book = dbWorker.query(book, com.fish.Book.class);
        System.out.println(book);
    
        List<Object> books = new ArrayList<>();
        books.add(new Book("蒙太奇手法", 129));
        books.add(new Book("音乐素养与教养", 72));
        books.add(new Book("中国为什么这么强大", 999));
        books.add(new Book("看世界", 69));
        books.add(new Book("读者", 12));
    
        // insert many books...
        dbWorker.insertAll(books.toArray());
        
        // batch
        dbWorker.workBatch(new String[] {
                "INSERT INTO book(name, price) VALUES('论三国', 23), ('孙子兵法', 24);",
                "INSERT INTO book(name, price) VALUE('世界之大宇宙之小', 89);"
        });
            
        // release resources and commit transaction...
        dbWorker.sleep(); 
    }
}
```

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ***bug*** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

### *2018-2-23:*<br/>
使用 DBManager.update(File) 方法进行更新配置文件时，由于 DBWorker 中的数据是静态的，也就是一加载这个类就决定好了，所以更新会失败，此时需要重新启动项目。。。这个问题已经修复！<br/>

if you use DBManager.update(File) to update infomaton of database, you will get a failed infomation. The reason is the connection in DBWorker is static, so you cant't change it after "new" it... Don't wrong, this bug has been fixed!<br/>



!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ***update*** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

### *2018-5-26:*<br/>
1. 抛弃了原本自己写的 log 类，转为使用 commons-logging 记录日志，目前实现类为 java.util.logging。<br/>
(My own log class is low-level... So I replace it with commons-logging, now its implement is java.util.logging)<br/>
2. 将项目转为了 maven 项目，方便拓展。<br/>
(I convert this project to maven project, for extending)<br/>
3. 项目发布版本分为两种，一种是不带依赖的 jar 包，占用空间很小；一种是带了全部依赖的 jar 包，方便引用。<br/>
(now it has two releases version: one is jar with no dependency, the other one is jar with dependencies)<br/>

### *2018-5-22:*<br/>
1. 新增了 workBatch(String[] sqls) 方法，现在可以批处理 sql 语句<br/>
(add a new method called workBatch(String[] sqls), now you can 'batch'...)<br/>
2. 新增了 insertAll(object[] objects) 方法，现在可以一次插入多个对象<br/>
(add a new method called insertAll(object[] objects), now you can insert many objects onetime)<br/>
3. 改进了 javadoc，以前写的 doc 丑的一比，现在规整多了。。。<br/>
(javadoc is good look, yeah, it is cuter :) ...)<br/>

### *2018-4-26:*<br/>
1. 修复了一个查询的隐藏 bug。<br/>
(fix a hidden bug...)<br/>
2. 新增了一个返回对象集合的方法，现在可以一次获取多个 bean 了。<br/>
(add a new method which can get many beans...)<br/>


### *2018-3-29:*<br/>
##### 这是一次重构改进！结合 DPCP 进行完善功能！<br/>
1. 加入了数据库连接池的功能，主要是 DPCP 连接池，所以配置文件也沿用了它的<br/>
2. 彻底改进了整个框架的结构: <br/>
    由原来的静态单例变成现在的管理单例加操作多例的结构，因此在并发模式下有了质的改变<br/>
    由原来的单个连接完成全部工作变成由连接池接管所有连接工作<br/>
    删除了很多冗余代码以及新增了一些功能<br/>
3. 完善了日志输出功能，并且对异常的处理更加细致<br/>



### *2018-3-21:*<br/>
##### 这是一次大整合大更新！结合 AutoMySQL 进行完善功能！<br/>
1. 整个用法和方法命名有所改进，整体更实用！<br/>
2. 加入了事务的处理，默认自动提交事务，也可进行设置！<br/>
3. 修复了之前插入的问题，现在的插入可以直接传入一个 orm 实体类对象，底层自动转化为数据表数据！<br/>
4. 由于加入了 AutoMySQL 框架，查询返回值可以直接返回实体类对象，而且无需强制类型转换！<br/>
5. 优化了代码的质量以及 javadoc 的质量！<br/>

*突然发现夸自己的作品也是一件很累的事情啊 ~手动滑稽:)~*<br/>

this version is perfect! hahaha, at lease before the next version!<br/>



### *2018-3-11:*<br/>
修改了查询的方法名，使得意思更加通熟易懂。<br/>
修正部分代码质量<br/>

update some function's name, such as query...<br/>
fix some codes<br/>


### *2018-3-10:*<br/>
加入了两个新的查询方法，返回 List<Map> 类型，之前的返回值为 List<String> ，如果要操作返回的数据是比较麻烦的，而在 Map 中操作数据只需要键值即可，在这个返回的 Map 中，key值就是数据库中的列名。<br/>
  
add two functions for querying, it returns List<Map> type, so you can get the exact data you want easily, because all the things you need is Key(the column of table is the key), and this is complex in the old version!<br/>


### *2018-2-24:*<br/> 
加入了英文注释<br/>

add some English introduction<br/>     


修改了部分功能：比如 work 可以手动指定文件，加入了 wakeUp 和 sleep 进行资源的管理。<br/>

=============================================================<br/>
#### params.md

* url<br/>
传递给JDBC驱动的用于建立连接的url<br/>
*ex: jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8*

* username<br/>
传递给JDBC驱动用于建立连接的用户名<br/>
*ex: root*

* password<br/>
传递给JDBC驱动用于建立连接的密码<br/>
*ex: xxx*

* maxActive<br/>
最大活动连接数:连接池在同一时间能够分配的最大活动连接的数量，如果设置为非正数，则表示不受限制<br/>
*ex: 30*

* maxIdle<br/>
最大空闲连接数:连接池中容许保持最大空闲状态的最大链接数量，操作的空闲连接将被释放，如果设置为负数表示不受限制<br/>
*ex: 10*

* minIdle<br/>
最小空闲连接数:连接池中容许保持最小空闲状态的最大链接数量，操作的空闲连接将被释放，
如果设置为负数表示不受限制<br/>
*ex: 1*

* maxWait<br/>
最大等待时间：当没有可用连接时，连接池等待连接被归还的最大(以毫秒计数),超过时间则抛出异常，
如果设置为-1则表示无限等待<br/>
*ex: 1000*

* initialSize<br/>
初始连接:池启动时创建的连接数量<br/>
*ex: 1*

* logAbandoned<br/>
连接被泄露时是否打印<br/>
*ex: true*

* removeAbandoned<br/>
是否自动回收超时连接<br/>
*ex: true*

* removeAbandoned Timeout<br/>
超时时间(以秒数为单位)<br/>
*ex: 10*

* timeBetweenEvictionRunsMillis<br/>
在空闲连接回收器线程运行期间休眠的时间值,以毫秒为单位<br/>
*ex: 1000*

* numTestsPerEvictionRun<br/>
在每次空闲连接回收器线程(如果有)运行时检查的连接数量<br/>
*ex: 10*

* minEvictableIdleTimeMillis<br/>
连接在池中保持空闲而不被空闲连接回收器线程<br/>
*ex: 10000*

<br/>
=============================================================<br/>
