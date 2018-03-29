# easyMySQL
## 一个简化的 mysql 数据库操作库。。。                                                                                     
## A simplified toolkit for mysql...


**最终可用版本在 out/artifacts/EasyMysql_jar 中**<br/>
the final version is in out/artifacts/EasyMysql_jar


**或者你也可以使用 EasyMysql.rar，里面包含了框架**<br/>
or you can download EasyMysql.rar, which is jars

### 下面演示了结合 AutoMySQL 小框架的查询操作：
#### if you use AutoMySQL and this jar:
##### （代码中的 Book.java 在源码中有提供）
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
    
            Book book = new Book();
            book.setName("奇异人生");
            book.setPrice(68);
    
            // insert a new book in database...
            //DBWorker.insert(book);
    
            // put whole table in a file...
            //DBWorker.putTableInFile("book", "Z:/book.txt");
    
            // query a book...
            book = DBWorker.query(book, Book.class);
            System.out.println(book);
    
            // release resources and commit transaction...
            DBWorker.sleep();
        }
    }


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ***bug*** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

### *2018-2-23:*<br/>
使用 DBManager.update(File) 方法进行更新配置文件时，由于 DBWorker 中的数据是静态的，也就是一加载这个类就决定好了，所以更新会失败，此时需要重新启动项目。。。这个问题已经修复！<br/>

if you use DBManager.update(File) to update infomaton of database, you will get a failed infomation. The reason is the connection in DBWorker is static, so you cant't change it after "new" it... Don't wrong, this bug has been fixed!<br/>



!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ***update*** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

正在加入数据库连接池功能，尽情期待！

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
=============================================================<br/>
