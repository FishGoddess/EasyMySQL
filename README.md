# easyMySQL
一个简化的 mysql 数据库操作库。。。                                                                                     
A simplified toolkit for mysql...

最终可用版本在 out/artifacts/EasyMysql_jar 中
the final version is in out/artifacts/EasyMysql_jar

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! bug !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

2018-2-23:                                                                                                                              
使用 DBManager.update(File) 方法进行更新配置文件时，由于 DBWorker 中的数据是静态的，也就是一加载这个类就决定好了，所以更新会失败，此时需要重新启动项目。。。这个问题已经修复！

if you use DBManager.update(File) to update infomaton of database, you will get a failed infomation. The reason is the connection in DBWorker is static, so you cant't change it after "new" it... Don't wrong, this bug has been fixed!

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! update !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

2018-2-24:                                                                                                                               
加入了英文注释                                                                                                                              
add some English introduction                                                                                                                  

修改了部分功能：比如 work 可以手动指定文件，加入了 wakeUp 和 sleep 进行资源的管理。