package com.fish.EasyMysql;

import core.MySQL;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一些数据库常用操作函数
 *
 * (CURD, you know, just some jobs about database...)
 *
 * @author Fish
 * */
public final class DBWorker
{
    private DBWorker()
    {}

    /**
     * 获得数据库连接
     *
     * (database's connection)
     * */
    private static Connection connection = null;

    /**
     * 数据库预处理语句
     *
     * (database's worker)
     * */
    private static PreparedStatement ps = null;

    /**
     * 从文件中读取 SQL 指令语句，返回成 String
     *
     * (read sql from a file)
     *
     * @return 返回得到的 SQL 语句 (return sql)
     * */
    private static String getSqlOrder(File sqlFile)
    {
        StringBuilder sql = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(sqlFile));

            String s = null;
            while ((s = br.readLine()) != null)
            {
                sql.append(s);
                sql.append("\n");
            }

            return sql.toString();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("打开文件失败！请检查文件是否存在！");
            return null;
        }
        catch (IOException e)
        {
            System.out.println("读取文件失败！请检查文件是否可读！");
            return null;
        }
    }

    /**
     * 叫醒工人，开始干活！
     * 连接到数据库
     *
     * (hey, man, wake up!)
     * (connect to database)
     * */
    public static void wakeUp()
    {
        connection = DBManager.getConnection();
    }

    /**
     * 干完活，不用他的话就让他睡觉休息一会吧！
     * 并且会提交事务，所以强烈建议使用这个方法代替直接的提交
     * 关闭连接，释放资源！建议在执行 DBManager.update(...) 之前先释放现有资源！
     *
     * (finish the jobs, let him have a break if you don't need him any more!)
     * (this method will commit transaction finally)
     * (close somethings and release resources! Invoke this method before invoking DBManager.update(...)!)
     */
    public static void sleep()
    {
        // 关闭前先提交事务！
        if (!DBManager.isAutoCommit())
        {
            commit();
        }

        try
        {
            if (ps != null)
            {
                ps.close();
            }

            if (connection != null)
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            System.out.println("执行语句关闭失败！可能你并没有叫他干活，他还不困 (斜眼笑:))");
            e.printStackTrace();
        }
    }

    /**
     * 提交事务
     *
     * (commit transaction)
     * */
    public static void commit()
    {
        try
        {
            connection.commit();
        }
        catch (SQLException e)
        {
            System.out.println("提交事务失败！");

            try
            {
                // 回滚事务！
                connection.rollback();
            }
            catch (SQLException ee)
            {
                System.out.println("事务回滚失败！");
            }
        }
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等
     * 命令语句由 sqlFile 文件传入
     * (exeute some complex works)
     * (sql will read from file automatically)
     *
     * @param sqlFile 要被执行的 sql 语句集合文件，包含一堆数据库执行语句
     *                (a file containing many sql orders that you want it to do)
     * @return 影响的行数，如果返回 -1，表示出现异常 (effected rows, -1 means error!)
     * */
    public static int work(File sqlFile)
    {
        return work(getSqlOrder(sqlFile));
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等
     * 如果新建一张已存在的表，会返回失败，但是不影响下面执行的语句，比如插入数据
     * (exeute some complex works)
     * (if you create a table existed, then you got a failure)
     * (But the next code will keep running, such as insert)
     *
     * @param sql 传入要执行的 sql 语句 (give it what you need to do)
     * @return 影响的行数，如果返回 -1，表示出现异常 (effected rows, -1 means error!)
     */
    public static int work(String sql)
    {
        // log...
        log(sql);

        try
        {
            Statement s = connection.createStatement();
            return s.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            System.out.println("命令执行失败！请检查 SQL 语法！也有可能是这个工人累死了！（斜眼笑）");
            return -1;
        }
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等
     * 如果新建一张已存在的表，会返回失败，但是不影响下面执行的语句，比如插入数据
     * (exeute some complex works)
     * (if you create a table existed, then you got a failure)
     * (But the next code will keep running, such as insert)
     *
     * @param sql 传入要执行的 sql 语句 (give it what you need to do)
     * @return 结果集 (result set)
     */
    private static ResultSet workQuery(String sql)
    {
        // log...
        log(sql);

        try
        {
            Statement s = connection.createStatement();
            return s.executeQuery(sql);
        }
        catch (SQLException e)
        {
            System.out.println("命令执行失败！请检查 SQL 语法！也有可能是这个工人累死了！（斜眼笑）");
            return null;
        }
    }

    /**
     * 插入表数据，根据传入对象的 @Table 和 @Column 值来插入 (插入时间会出错！)
     * (insert data, depending on the value of @Table and @Column)
     *
     * @param data 插入对象，这必须是一个标准 javabean 对象，
     *               在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得
     *               (orm object, this a standar javabean, also, it has @Table whose value is the table name,
     *               and @Column with the column of this table)
     * @return 影响的行数，如果返回 -1，表示出现异常 (effected rows, -1 means error!)
     */
    public static int insert(Object data)
    {
        return work(MySQL.insertSQL(data));
    }

    /**
     * 从数据库删除一行数据
     *
     * (delete some data)
     *
     * @param tableName 要删除内容的表名 (table name)
     * @param selection 要删除的内容的筛选条件
     *                  比如，你要删除 id = 1 的元素，在这个地方就传入 "id = 1"；
     *                  也就是说：列名 = 列内容;
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";
     *                  这里需要注意，如果传入 1，就会删除整份表，为了防止误操作导致删除整份表，在这里特意对 "1" 做了处理，
     *                  如果你要删除整份表，传入 "1" 将是无效的，需要传入 "ALL" 才能执行！
     *                  (delete data depending on your selection)
     * @return true 删除成功 (Done!)，false 删除失败 (you failed!)
     */
    public static boolean delete(String tableName, String selection)
    {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");

        // 防止误操作导致删除整份表！
        if ("1".equals(selection))
        {
            System.out.println("警告：您传入的值是 1，这将会删除整份表！");
            System.out.println("为了保证您是真的想删除而不是误操作，请在传参时将 selection 传入值为 \"ALL\"!");
            return false;
        }

        if ("ALL".equalsIgnoreCase(selection))
        {
            selection = "1";
        }

        sql.append(selection + ";");

        try
        {
            ps = connection.prepareStatement(sql.toString());

            return ps.execute();
        }
        catch (SQLException e)
        {
            System.out.println("删除失败！请检查选择器是否输入正确！");
            return false;
        }
    }

    /**
     * 更新表数据，根据传入对象的 @Table 和 @Column 值来更新
     *
     * (update data, depending on the value of @Table and @Column)
     *
     * @param oldData 旧的对象，这必须是一个标准 javabean 对象，
     *               在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得，
     *               并且这个对象必须进行赋值，否则无法找到对应的数据，有可能导致整张表被覆盖
     *               (old object, this a standar javabean, also, it has @Table whose value is the table name,
     *               and @Column with the column of this table, and its field must be not null!)
     * @param newData 新的对象，这必须是一个标准 javabean 对象，
     *               在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得
     *               (old object, this a standar javabean, also, it has @Table whose value is the table name,
     *               and @Column with the column of this table)
     * @return 影响的行数，如果返回 -1，表示出现异常 (effected rows, -1 means error!)
     */
    public static int update(Object oldData, Object newData)
    {
        return work(MySQL.updateSQL(oldData, newData));
    }

    /**
     * 向数据库查询数据
     *
     * (query some data)
     *
     * @param tableName 要查询内容的表名 table name
     * @param selection 要查询的内容的筛选条件
     *                  比如，你要查询 id = 1 的元素，在这个地方就传入 "id = 1"；
     *                  也就是说：列名 = 列内容;
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";
     *                  这里需要注意，如果传入 "1" 或 "ALL"，就会查询整份表，把整份表包装到字符串中
     *                  (query data depending on your selection)
     * @return 返回查询到的结果，字符串封装 (return strings that filled with data)
     */
    public static List<String> queryStrings(String tableName, String selection)
    {
        List<String> result = new ArrayList<>();
        List<Map> maps = queryMaps(tableName, selection);

        for (int i = 0; i < maps.size(); i++)
        {
            result.add(maps.get(i).toString());
        }

        return result;
    }

    /**
     * 向数据库查询数据表
     *
     * (query some data)
     *
     * @param tableName 要查询内容的表名 (table name)
     * @return 返回查询到的结果，字符串封装 (return strings that filled with data)
     */
    public static List<String> queryStrings(String tableName)
    {
        return queryStrings(tableName, "ALL");
    }

    /**
     * 向数据库查询数据
     *
     * (query some data)
     *
     * @param tableName 要查询内容的表名 (table name)
     * @param selection 要查询的内容的筛选条件
     *                  比如，你要查询 id = 1 的元素，在这个地方就传入 "id = 1"；
     *                  也就是说：列名 = 列内容;
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";
     *                  这里需要注意，如果传入 "1" 或 "ALL"，就会查询整份表，把整份表包装到字符串中
     *                  (query data depending on your selection)
     * @return 返回查询到的结果，HashMap 封装，可通过键值获取到具体的值，键值就是表的列名
     *         (the key is the column of this table)
     */
    public static List<Map> queryMaps(String tableName, String selection)
    {
        List<Map> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append(tableName);

        if ("ALL".equalsIgnoreCase(selection))
        {
            selection = "1";
        }

        sql.append(" WHERE " + selection + ";");

        try
        {
            // log...
            log(sql.toString());

            ps = connection.prepareStatement(sql.toString());

            ResultSet rs = ps.executeQuery();
            Map<String, Object> map = null;
            ResultSetMetaData rsmd = rs.getMetaData();

            // 计算列数
            int columns = rsmd.getColumnCount();

            while (rs.next())
            {
                map = new HashMap<>();
                for (int i = 1; i <= columns; i++)
                {
                    map.put(rsmd.getColumnName(i), rs.getObject(i));
                }

                result.add(map);
            }

            return result;
        }
        catch (SQLException e)
        {
            System.out.println("查询失败！请检查传入的选择器是否有效！");
            return null;
        }
    }

    /**
     * 向数据库查询数据表
     *
     * (query some data)
     *
     * @param tableName 要查询内容的表名 (table name)
     * @return 返回查询到的结果，HashMap 封装，可通过键值获取到具体的值，键值就是表的列名
     *         (the key is the column of this table)
     */
    public static List<Map> queryMaps(String tableName)
    {
        return queryMaps(tableName, "ALL");
    }

    /**
     * 获取表数据，根据传入对象的 @Table 和 @Column 值来查询
     *
     * (query data, depending on the value of @Table and @Column)
     *
     * @param data 查询依据对象，这必须是一个标准 javabean 对象，
     *               在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得
     *               (orm object, this a standar javabean, also, it has @Table whose value is the table name,
     *               and @Column with the column of this table)
     * @param clazz 这个实体类的类型，T 就是具体类型，比如 Book.class
     *              (the type of this entity, T is real type, such as Book.class)
     * @param <T> T 就是具体类型，比如 Book.class
     *            (T is real type, such as Book.class)
     * @return 返回实体对象 (return a entity object)
     */
    public static <T> T query(Object data, Class<T> clazz)
    {
        ResultSet rs = workQuery(MySQL.querySQL(data));
        Map<String, Object> map = null;

        if (rs != null)
        {
            try
            {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columns = rsmd.getColumnCount();
                while (rs.next())
                {
                    map = new HashMap<>();
                    for (int i = 1; i <= columns; i++)
                    {
                        map.put(rsmd.getColumnName(i), rs.getObject(i));
                    }
                }
            }
            catch (SQLException e)
            {
                System.out.println("封装对象失败！");
            }
        }

        return DBManager.getBean(map, clazz);
    }

    /**
     * 输出日志，暂时只能显示 sql 语句
     *
     * (output the sql)
     * */
    private static void log(String sql)
    {
        System.err.println("(log ===> sql : " + sql + ")");
    }

    /**
     * 将查询到的一整个表保存进文件中
     *
     * (save the whole table into a file)
     *
     * @param tableName 要查询的一整份表 (table name)
     * @param filePath 文件保存路径，包括文件名，合法的取值：Z:/table.txt
     *                 (saving path, including file name, like "Z:/table.txt")
     * @return true 保存成功 (Done!)，false 保存失败 (you failed!)
     * */
    public static boolean putTableInFile(String tableName, String filePath)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath)));

            List<String> list = queryStrings(tableName);
            for (String s : list)
            {
                bw.write(s);
                bw.newLine();
            }

            bw.flush();
            bw.close();
            return true;
        }
        catch (IOException e)
        {
            System.out.println("文件写出异常！请检查路径！");
            return false;
        }
    }
}
