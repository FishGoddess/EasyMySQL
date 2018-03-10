# easyMySQL
## 一个简化的 mysql 数据库操作库。。。                                                                                     
## A simplified toolkit for mysql...

**最终可用版本在 out/artifacts/EasyMysql_jar 中**<br/>
the final version is in out/artifacts/EasyMysql_jar

**或者你也可以使用 EasyMysql.rar，里面包含了框架**<br/>
or you can download EasyMysql.rar, which is jars

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ***bug*** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

### *2018-2-23:*<br/>
使用 DBManager.update(File) 方法进行更新配置文件时，由于 DBWorker 中的数据是静态的，也就是一加载这个类就决定好了，所以更新会失败，此时需要重新启动项目。。。这个问题已经修复！

if you use DBManager.update(File) to update infomaton of database, you will get a failed infomation. The reason is the connection in DBWorker is static, so you cant't change it after "new" it... Don't wrong, this bug has been fixed!

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ***update*** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

### *2018-3-10:*<br/>
加入了两个新的查询方法，返回 List<Map> 类型，之前的返回值为 List<String> ，如果要操作返回的数据是比较麻烦的，而在 Map 中操作数据只需要键值即可，在这个返回的 Map 中，key值就是数据库中的列名。
add two functions for querying, it returns List<Map> type, so you can get the exact data you want easily, because all the things you need is Key(the column of table is the key), and this is complex in the old version!

### *2018-2-24:*<br/> 
加入了英文注释<br/>
add some English introduction<br/>     

修改了部分功能：比如 work 可以手动指定文件，加入了 wakeUp 和 sleep 进行资源的管理。<br/>
