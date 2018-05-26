package com.fish.core;

import com.fish.log.Logger;
import core.MySQL;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一些数据库常用操作函数<br>
 * <p>
 * (CURD, you know, just some jobs about database...)<br>
 *
 * @author Fish
 */
public final class DBWorker
{
    /**
     * 叫醒工人，开始干活！<br>
     * 连接到数据库<br>
     * <p>
     * (hey, man, wake up!)<br>
     * (connect to database)<br>
     */
    DBWorker(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * 获得数据库连接<br>
     * <p>
     * (database's connection)<br>
     */
    private Connection connection = null;

    /**
     * 数据库预处理语句<br>
     * <p>
     * (database's worker)<br>
     */
    private PreparedStatement ps = null;

    /**
     * 从文件中读取 SQL 指令语句，返回成 String<br>
     * <p>
     * (read sql from a file)<br>
     *
     * @return 返回得到的 SQL 语句<br>
     *     (return sql)<br>
     */
    private String getSqlOrder(File sqlFile)
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
     * 干完活，不用他的话就让他睡觉休息一会吧！<br>
     * 并且会提交事务，所以强烈建议使用这个方法代替直接的提交<br>
     * 关闭连接，释放资源！建议在执行 DBManager.update(...) 之前先释放现有资源！<br>
     * <p>
     * (finish the jobs, let him have a break if you don't need him any more!)<br>
     * (this method will commit transaction finally)<br>
     * (close somethings and release resources! Invoke this method before invoking DBManager.update(...)!)<br>
     */
    public void sleep()
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
            Logger.warn("资源释放失败！可能你并没有叫他干活，他还不困 (斜眼笑:))");
        }
    }

    /**
     * 提交事务<br>
     * <p>
     * (commit transaction)<br>
     */
    public void commit()
    {
        try
        {
            connection.commit();
        }
        catch (SQLException e)
        {
            Logger.warn("提交事务失败！");

            try
            {
                // 回滚事务！
                connection.rollback();
            }
            catch (SQLException ee)
            {
                Logger.err("事务回滚失败！");
            }
        }
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库等等<br>
     * 命令语句由 sqlFile 文件传入<br>
     * (exeute some complex works)<br>
     * (sql will read from file automatically)<br>
     *
     * @param sqlFile 要被执行的 sql 语句集合文件，包含一堆数据库执行语句<br>
     *                (a file containing many sql orders that you want it to do)<br>
     * @return 影响的行数，如果返回 -1，表示出现异常<br>
     * (effected rows, -1 means error!)<br>
     */
    public int work(File sqlFile)
    {
        return work(getSqlOrder(sqlFile));
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等<br>
     * 如果新建一张已存在的表，会返回失败，但是不影响下面执行的语句，比如插入数据<br>
     * (exeute some complex works)<br>
     * (if you create a table existed, then you got a failure)<br>
     * (But the next code will keep running, such as insert)<br>
     *
     * @param sql 传入要执行的 sql 语句 (give it what you need to do)<br>
     * @return 影响的行数，如果返回 -1，表示出现异常<br>
     * (effected rows, -1 means error!)<br>
     */
    public int work(String sql)
    {
        return workBatch(new String[] {sql})[0];
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等<br>
     * 如果新建一张已存在的表，会返回失败，但是不影响下面执行的语句，比如插入数据<br>
     * (exeute some complex works)<br>
     * (if you create a table existed, then you got a failure)<br>
     * (But the next code will keep running, such as insert)<br>
     *
     * @param sqls 传入要执行的 sql 语句集合 (give it what you need to do)<br>
     * @return 影响的行数集合，如果返回 {-1}，表示出现异常<br>
     * (effected rows, {-1} means error!)<br>
     */
    public int[] workBatch(String[] sqls)
    {
        try
        {
            Statement s = connection.createStatement();
            for (String sql : sqls)
            {
                // log...
                Logger.info("sql: " + sql);

                s.addBatch(sql);
            }
            int[] affects = s.executeBatch();
            s.clearBatch(); // clear the batch cache...

            return affects;
        }
        catch (SQLException e)
        {
            Logger.err("命令执行失败！请检查 SQL 语法！也有可能是这个工人累死了！（斜眼笑）");
            return new int[] {-1};
        }
    }

    /**
     * 执行较为复杂的数据库命令，比如高精度优化查询数据等等<br>
     * 命令语句由 sqlFile 文件传入<br>
     * (exeute some complex works)<br>
     * (sql will read from file automatically)<br>
     *
     * @param sqlFile 要被执行的 sql 语句集合文件，包含一堆数据库执行语句<br>
     *                (a file containing many sql orders that you want it to do)<br>
     * @return 结果集<br>(result set)<br>
     */
    public ResultSet workQuery(File sqlFile)
    {
        return workQuery(getSqlOrder(sqlFile));
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等<br>
     * 如果新建一张已存在的表，会返回失败，但是不影响下面执行的语句，比如插入数据<br>
     * (exeute some complex works)<br>
     * (if you create a table existed, then you got a failure)<br>
     * (But the next code will keep running, such as insert)<br>
     *
     * @param sql 传入要执行的 sql 语句 (give it what you need to do)<br>
     * @return 结果集<br>(result set)<br>
     */
    public ResultSet workQuery(String sql)
    {
        // log...
        Logger.info("sql: " + sql);

        try
        {
            Statement s = connection.createStatement();
            return s.executeQuery(sql);
        }
        catch (SQLException e)
        {
            Logger.warn("命令执行失败！请检查 SQL 语法！也有可能是这个工人累死了！（斜眼笑）");
            return null;
        }
    }

    /**
     * 插入表数据，根据传入对象的 @Table 和 @Column 值来插入 (插入时间会出错！)<br>
     * (insert data, depending on the value of @Table and @Column)<br>
     *
     * @param data 插入对象，这必须是一个标准 javabean 对象，<br>
     *             在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得<br>
     *             (orm object, this a standar javabean, also, it has @Table whose value is the table name,<br>
     *             and @Column with the column of this table)<br>
     * @return 影响的行数，如果返回 -1，表示出现异常<br>
     * (effected rows, -1 means error!)<br>
     */
    public int insert(Object data)
    {
        return work(MySQL.insertSQL(data));
    }

    /**
     * 插入表数据，根据传入对象的 @Table 和 @Column 值来插入 (插入时间会出错！)<br>
     * (insert data, depending on the value of @Table and @Column)<br>
     *
     * @param objects 插入对象集合，里面的元素必须是标准 javabean 对象，<br>
     *             在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得<br>
     *             (orm object, they must be standar javabeans, also, it has @Table whose value is the table name,<br>
     *             and @Column with the column of this table)<br>
     * @return 影响的行数，如果返回 -1，表示出现异常<br>
     * (effected rows, -1 means error!)<br>
     */
    public int insertAll(Object[] objects)
    {
        return work(MySQL.insertSQLs(objects));
    }

    /**
     * 从数据库删除一行数据<br>
     * <p>
     * (delete some data)<br>
     *
     * @param tableName 要删除内容的表名 (table name)<br>
     * @param selection 要删除的内容的筛选条件<br>
     *                  比如，你要删除 id = 1 的元素，在这个地方就传入 "id = 1"；<br>
     *                  也就是说：列名 = 列内容;<br>
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";<br>
     *                  这里需要注意，如果传入 1，就会删除整份表，为了防止误操作导致删除整份表，在这里特意对 "1" 做了处理，<br>
     *                  如果你要删除整份表，传入 "1" 将是无效的，需要传入 "ALL" 才能执行！<br>
     *                  (delete data depending on your selection)<br>
     * @return true 删除成功 (Done!)，false 删除失败 (you failed!)....<br>
     *     Maybe this is what you think, but you are wrong! It's always false!<br>
     */
    public boolean delete(String tableName, String selection)
    {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");

        // 防止误操作导致删除整份表！
        if ("1".equals(selection))
        {
            Logger.warn("警告：您传入的值是 1，这将会删除整份表！");
            Logger.warn("为了保证您是真的想删除而不是误操作，请在传参时将 selection 传入值为 \"ALL\"!");
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
            Logger.err("删除失败！请检查选择器是否输入正确！");
            return false;
        }
    }

    /**
     * 更新表数据，根据传入对象的 @Table 和 @Column 值来更新<br>
     * <p>
     * (update data, depending on the value of @Table and @Column)<br>
     *
     * @param oldData 旧的对象，这必须是一个标准 javabean 对象，<br>
     *                在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得，<br>
     *                并且这个对象必须进行赋值，否则无法找到对应的数据，有可能导致整张表被覆盖<br>
     *                (old object, this a standar javabean, also, it has @Table whose value is the table name,<br>
     *                and @Column with the column of this table, and its field must be not null!)<br>
     * @param newData 新的对象，这必须是一个标准 javabean 对象，<br>
     *                在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得<br>
     *                (old object, this a standar javabean, also, it has @Table whose value is the table name,<br>
     *                and @Column with the column of this table)<br>
     * @return 影响的行数，如果返回 -1，表示出现异常<br>(effected rows, -1 means error!)<br>
     */
    public int update(Object oldData, Object newData)
    {
        return work(MySQL.updateSQL(oldData, newData));
    }

    /**
     * 向数据库查询数据<br>
     * <p>
     * (query some data)<br>
     *
     * @param tableName 要查询内容的表名 table name<br>
     * @param selection 要查询的内容的筛选条件<br>
     *                  比如，你要查询 id = 1 的元素，在这个地方就传入 "id = 1"；<br>
     *                  也就是说：列名 = 列内容;<br>
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";<br>
     *                  这里需要注意，如果传入 "1" 或 "ALL"，就会查询整份表，把整份表包装到字符串中<br>
     *                  (query data depending on your selection)<br>
     * @return 返回查询到的结果，字符串封装<br>(return strings that filled with data)<br>
     */
    public List<String> queryStrings(String tableName, String selection)
    {
        List<String> result = new ArrayList<>();
        List<Map<String, Object>> maps = queryMaps(tableName, selection);

        for (int i = 0; i < maps.size(); i++)
        {
            result.add(maps.get(i).toString());
        }

        return result;
    }

    /**
     * 向数据库查询数据表<br>
     * <p>
     * (query some data)<br>
     *
     * @param tableName 要查询内容的表名 (table name)<br>
     * @return 返回查询到的结果，字符串封装 (return strings that filled with data)<br>
     */
    public List<String> queryStrings(String tableName)
    {
        return queryStrings(tableName, "ALL");
    }

    /**
     * 向数据库查询数据<br>
     * <p>
     * (query some data)<br>
     *
     * @param tableName 要查询内容的表名 (table name)<br>
     * @param selection 要查询的内容的筛选条件<br>
     *                  比如，你要查询 id = 1 的元素，在这个地方就传入 "id = 1"；<br>
     *                  也就是说：列名 = 列内容;<br>
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";<br>
     *                  这里需要注意，如果传入 "1" 或 "ALL"，就会查询整份表，把整份表包装到字符串中<br>
     *                  (query data depending on your selection)<br>
     * @return 返回查询到的结果，HashMap 封装，可通过键值获取到具体的值，键值就是表的列名<br>
     * (the key is the column of this table)<br>
     */
    public List<Map<String, Object>> queryMaps(String tableName, String selection)
    {
        List<Map<String, Object>> result = new ArrayList<>();
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
            Logger.info("sql: " + sql.toString());

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
            Logger.warn("查询失败！请检查传入的选择器是否有效！");
            return null;
        }
    }

    /**
     * 向数据库查询数据表<br>
     * <p>
     * (query some data)<br>
     *
     * @param tableName 要查询内容的表名 (table name)<br>
     * @return 返回查询到的结果，HashMap 封装，可通过键值获取到具体的值，键值就是表的列名<br>
     * (the key is the column of this table)<br>
     */
    public List<Map<String, Object>> queryMaps(String tableName)
    {
        return queryMaps(tableName, "ALL");
    }

    /**
     * 获取表数据，根据传入对象的 @Table 和 @Column 值来查询<br>
     * <p>
     * (query data, depending on the value of @Table and @Column)<br>
     *
     * @param data  查询依据对象，这必须是一个标准 javabean 对象，<br>
     *              在类上使用 @Table 注解，并传入对应的表名，而对应的列名则是通过 @Column 来获得<br>
     *              (orm object, this a standar javabean, also, it has @Table whose value is the table name,<br>
     *              and @Column with the column of this table)<br>
     * @param clazz 这个实体类的类型，T 就是具体类型，比如 com.fish.Book.class<br>
     *              (the type of this entity, T is real type, such as com.fish.Book.class)<br>
     * @param <T>   T 就是具体类型，比如 com.fish.Book.class<br>
     *              (T is real type, such as com.fish.Book.class)<br>
     * @return 返回实体对象<br>(return a entity object)<br>
     */
    public <T> T query(Object data, Class<T> clazz)
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
                Logger.warn("封装对象失败！");
            }
        }

        return MySQL.getBeanByMap(map, clazz);
    }

    /**
     * 向数据库查询数据表<br>
     * <p>
     * (query some data)<br>
     *
     * @param tableName 要查询内容的表名 (table name)<br>
     * @param clazz     这个实体类的类型，T 就是具体类型，比如 com.fish.Book.class<br>
     *                  (the type of this entity, T is real type, such as com.fish.Book.class)<br>
     * @param <T>       T 就是具体类型，比如 com.fish.Book.class<br>
     *                  (T is real type, such as com.fish.Book.class)<br>
     * @return 返回查询到的结果，List 封装，可直接获取对象集合<br>
     * (you got many beans one time...)<br>
     */
    public <T> List<T> queryAll(String tableName, Class<T> clazz)
    {
        return MySQL.getBeansByMaps(queryMaps(tableName), clazz);
    }

    /**
     * 将查询到的一整个表保存进文件中<br>
     * <p>
     * (save the whole table into a file)<br>
     *
     * @param tableName 要查询的一整份表 (table name)<br>
     * @param filePath  文件保存路径，包括文件名，合法的取值：Z:/table.txt<br>
     *                  (saving path, including file name, like "Z:/table.txt")<br>
     * @return true 保存成功 (Done!)，false 保存失败 (you failed!)<br>
     */
    public boolean putTableInFile(String tableName, String filePath)
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
            Logger.warn("文件写出异常！请检查路径！");
            return false;
        }
    }
}
