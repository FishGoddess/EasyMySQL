package com.fish.EasyMysql;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 一些数据库常用操作函数
 * CURD, you know, just some jobs about database...
 * @author Fish
 * */
public final class DBWorker
{
    private DBWorker()
    {}

    /**
     * 获得数据库连接
     * database's connection
     * */
    private static Connection connection = null;

    /**
     * 数据库预处理语句
     * database's worker
     * */
    private static PreparedStatement ps = null;

    /**
     * 计算传入的字段有多少个，替换成 “?” 占位符输出
     * you don't need to know what it means... hahaha, find a Chinese dictionary to help you!
     * @param oneColumn 传入的一行内容
     * @return 返回占位符
     * */
    private static String getAttrs(String[] oneColumn)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < oneColumn.length; i++)
        {
            sb.append("?");

            if (i != oneColumn.length - 1)
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * 从文件中读取 SQL 指令语句，返回成 String
     * read sql from a file
     * @return 返回得到的 SQL 语句
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
     * hey, man, wake up!
     * 连接到数据库
     * connect to database
     * */
    public static void wakeUp()
    {
        connection = DBManager.getConnection();
    }

    /**
     * 干完活，不用他的话就让他睡觉休息一会吧！
     * finish the jobs, let him have a break if you don't need him any more!
     * 关闭连接，释放资源！建议在执行 DBManager.update(...) 之前先释放现有资源！
     * close somethings and release resources! Invoke this method before invoking DBManager.update(...)!
     */
    public static void sleep()
    {
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
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等
     * exeute some complex works
     * 命令语句由 sqlFile 文件传入
     * sql will read from file automatically
     * @return true 执行成功，false 执行失败
     * */
    public static boolean work(File sqlFile)
    {
        return work(getSqlOrder(sqlFile));
    }

    /**
     * 执行较为复杂的数据库命令，比如建表，删除数据库，创建并执行事务等等
     * 如果新建一张已存在的表，会返回失败，但是不影响下面执行的语句，比如插入数据
     * exeute some complex works
     * if you create a table existed, then you got a failure
     * But the next code will keep running, such as insert
     * @param sql 传入要执行的 sql 语句
     * @return true 执行成功，false 执行失败
     */
    public static boolean work(String sql)
    {
        try
        {
            Statement s = connection.createStatement();
            return s.execute(sql);
        }
        catch (SQLException e)
        {
            System.out.println("命令执行失败！请检查 SQL 语法！也有可能是这个工人累死了！（斜眼笑）");
            return false;
        }
    }

    /**
     * 向数据库插入一行数据 (插入时间会出错！)
     * insert some data
     * @param tableName 要插入的表名 table name
     * @param oneColumn 要插入的一行内容，包括主键，比如说 id
     *                  示例：
     *                  向 goods_cates 表插入一行数据，“8” 和 “手机” 是行内元素
     *                  DBWorker.insert("goods_cates", new String[] {"8", "手机"});
     *                  the data you want to insert
     *                  the whole column data, including primary key like "id"!
     * @return true 插入成功，false 插入失败
     * */
    public static boolean insert(String tableName, String[] oneColumn)
    {
        StringBuilder sql = new StringBuilder("INSERT ");
        sql.append(tableName);
        sql.append(" VALUES(" + getAttrs(oneColumn) + ");");

        try
        {
            ps = connection.prepareStatement(sql.toString());

            for (int i = 1; i <= oneColumn.length; i++)
            {
                ps.setObject(i, oneColumn[i - 1]);
            }

            return ps.execute();
        }
        catch (SQLException e)
        {
            System.out.println("插入失败！请检查需要插入的内容是否正确！");
            return false;
        }
    }

    /**
     * 从数据库删除一行数据
     * delete some data
     * @param tableName 要删除内容的表名 table name
     * @param selection 要删除的内容的筛选条件
     *                  比如，你要删除 id = 1 的元素，在这个地方就传入 "id = 1"；
     *                  也就是说：列名 = 列内容;
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";
     *                  这里需要注意，如果传入 1，就会删除整份表，为了防止误操作导致删除整份表，在这里特意对 "1" 做了处理，
     *                  如果你要删除整份表，传入 "1" 将是无效的，需要传入 "ALL" 才能执行！
     *                  delete data depending on your selection
     * @return true 删除成功，false 删除失败
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
     * 从数据库修改一行数据
     * update some data
     * @param tableName 要被修改数据的表名 table name
     * @param columnName 要被修改的列名称 the column you want to update
     * @param newValue 新设定的值 the new value you want to set
     * @param oldValue 旧的值 the old value, which can find the data you want to update
     *                 示例：
     *                 从 goods_cates 表中修改一行数据，把 cate_name = "笔记本配件" 修改成 cate_name = "手机"
     *                 int count = DBWorker.update("goods_cates", "cate_name", "笔记本配件", "手机");
     *                 System.out.println(count); // 打印出修改的行数
     * @return 修改影响的行数 the rows effected
     */
    public static int update(String tableName, String columnName, String oldValue, String newValue)
    {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName);
        sql.append(" SET " + columnName + " = \"" + newValue + "\"");
        sql.append(" WHERE " + columnName + " = \"" + oldValue + "\";");

        try
        {
            ps = connection.prepareStatement(sql.toString());

            return ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("更新数据失败！请检查需要更新的行内容是否正确！");
            return 0;
        }
    }

    /**
     * 向数据库查询数据
     * query some data
     * @param tableName 要查询内容的表名 table name
     * @param selection 要查询的内容的筛选条件
     *                  比如，你要查询 id = 1 的元素，在这个地方就传入 "id = 1"；
     *                  也就是说：列名 = 列内容;
     *                  如果列内容是字符串，则需要使用 (\") 把字符串包住，比如 name = \"选择器\";
     *                  这里需要注意，如果传入 "1" 或 "ALL"，就会查询整份表，把整份表包装到字符串中
     *                  query data depending on your selection
     * @return 返回查询到的结果，字符串封装
     */
    public static List<String> query(String tableName, String selection)
    {
        List<String> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append(tableName);

        if ("ALL".equalsIgnoreCase(selection))
        {
            selection = "1";
        }

        sql.append(" WHERE " + selection + ";");

        try
        {
            // 计算列数
            ps = connection.prepareStatement("DESC " + tableName);
            int columns = ps.executeUpdate();

            ps = connection.prepareStatement(sql.toString());

            StringBuilder sb = null;
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next())
            {
                sb = new StringBuilder();
                for (int i = 1; i <= columns; i++)
                {
                    sb.append("[" + rsmd.getColumnName(i) + ": " + rs.getObject(i) + "]");

                    if (i != columns)
                    {
                        sb.append(", ");
                    }
                }

                result.add(sb.toString());
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
     * query some data
     * @param tableName 要查询内容的表名 table name
     * @return 返回查询到的结果，字符串封装
     */
    public static List<String> query(String tableName)
    {
        return query(tableName, "1");
    }

    /**
     * 将查询到的一整个表保存进文件中
     * save the whole table into a file
     * @param tableName 要查询的一整份表 table name
     * @param filePath 文件保存路径，包括文件名，合法的取值：Z:/table.txt
     *                 saving path, including file name, like "Z:/table.txt"
     * @return true 保存成功，false 保存失败
     * */
    public static boolean putTableInFile(String tableName, String filePath)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath)));

            List<String> list = query(tableName);
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
