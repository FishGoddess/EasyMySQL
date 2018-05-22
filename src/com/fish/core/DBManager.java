package com.fish.core;

import com.fish.exception.UninitializedException;
import com.fish.log.Logger;
import core.MySQL;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * 管理 MySql 数据库的连接信息<br>
 *
 * (some infomation about database's connection)<br>
 *
 * @author Fish
 * */
public final class DBManager
{
    /**
     * 标记是否已经初始化，除非调用 update 方法，否则该变量不应该被改写
     *
     * (mark the status of initation)
     * */
    private static volatile boolean isInited = false;

    /**
     * 配置文件所在路径
     *
     * (where the config file hides?)
     * */
    private static volatile File filePath = null;

    /**
     * 是否自动提交
     *
     * (if auto commit)
     * */
    private static volatile boolean autoCommit = false;

    /**
     * 数据库连接池
     *
     *
     * */
    private static volatile DataSource dataSource = null;

    /**
     * 不需要存在这个类的对象，只需要调用所需的方法即可<br>
     *
     * (single dog, single dog, single all the way! Which song you are reminded of?)<br>
     * */
    private DBManager()
    {}

    /**
     * 初始化数据库管理器<br>
     *
     * (init this guy)<br>
     *
     * @param path 指定配置文件的路径 (the home of config file)<br>
     * @return true 初始化成功 (good job!)， false 初始化失败 (you failed!)<br>
     * */
    public static synchronized boolean init(File path)
    {
        if (!isInited)
        {
            filePath = path;
            //Config.url = new StringBuilder();

            try
            {
                // 这个配置文件和 dpcp 的一样
                Properties properties = new Properties();
                properties.load(new BufferedInputStream(new FileInputStream(filePath)));
                properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");

                dataSource = BasicDataSourceFactory.createDataSource(properties);

                isInited = true;
            }
            catch (IOException e)
            {
                System.err.println("配置文件读取异常");
                System.exit(1);
            }
            catch (Exception ex)
            {
                System.err.println("数据库连接池创建失败！");
                System.exit(1);
            }
        }

        return true;
    }

    /**
     * 检查是否已经初始化，否则抛出未初始化异常<br>
     *
     * (check if inited, or throws UninitializedException)<br>
     *
     * @throws UninitializedException 未初始化异常 (UninitializedException)<br>
     * */
    private static synchronized void checkIfInited() throws UninitializedException
    {
        // 判断是否已经初始化管理器
        if (!isInited)
        {
            throw new UninitializedException("You must invoke 'DBManager.init(File file)' to init me!");
        }
    }

    /**
     * 根据指定文件更新数据库管理器的配置信息<br>
     * 在调用该方法之前，建议先调用 DBWorker.sleep() 释放资源！<br>
     *
     * (update connection depending on what config you give)<br>
     * (Maybe you should invoke DBWorker.sleep() before invoking this method!)<br>
     *
     * @param path 指定配置文件的路径 (the home of config file)<br>
     * @throws UninitializedException 未初始化异常 (UninitializedException)<br>
     * @return true 更新成功 (Done!)， false 更新失败 (you failed!)<br>
     * */
    public static synchronized boolean update(File path)
    {
        // check...
        checkIfInited();

        isInited = false;
        filePath = path;

        return init(filePath);
    }

    /**
     * 更新数据库管理器的配置信息，按照第一次初始化的路径进行更新<br>
     * 在调用该方法之前，建议先调用 DBWorker.sleep() 释放资源！<br>
     *
     * (update connection using the old config file)<br>
     * (Maybe you should invoke DBWorker.sleep() before invoking this method!)<br>
     *
     * @throws UninitializedException 未初始化异常 (UninitializedException)<br>
     * @return true 更新成功 (Done!)， false 更新失败 (you failed!)<br>
     */
    public static synchronized boolean update()
    {
        // check...
        checkIfInited();

        isInited = false;

        return init(filePath);
    }

    /**
     * 获取数据库连接, 不推荐外界直接使用<br>
     *
     * (get a connection of database, don't advise!)<br>
     *
     * @throws UninitializedException 如果你没有先调用初始化方法就会触发这个异常未初始化异常 (UninitializedException)<br>
     * @return 返回得到的数据库连接<br>
     *     (you get a connection)<br>
     * */
    public static synchronized Connection getConnection() throws UninitializedException
    {
        // check...
        checkIfInited();

        try
        {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(autoCommit);

            return connection;
        }
        catch (SQLException e)
        {
            Logger.err("创建连接失败！请检查地址或用户名密码的正确性以及网络是否可用！");
        }

        /*try
        {
            // 加载数据库连接驱动
            Class.forName(Config.DRIVER);
            Connection connection = DriverManager.getConnection(Config.url.toString(), Config.user, Config.password);
            connection.setAutoCommit(autoCommit);

            return connection;
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("驱动加载失败！请检查驱动是否可用！");
            return null;
        }
        catch (SQLException e)
        {
            System.out.println("创建连接失败！请检查地址或用户名密码的正确性以及网络是否可用！");
            return null;
        }*/

        return null;
    }

    /**
     * 获取当前事务的提交方式，默认是自动提交<br>
     *
     * (get transaction state, default auto commit)<br>
     *
     * @return 返回是否自动提交事务<br>
     *     (the state)<br>
     * */
    public static synchronized boolean isAutoCommit()
    {
        return autoCommit;
    }

    /**
     * 设置事务提交方式，默认是自动提交<br>
     *
     * (get transaction state, default auto commit)<br>
     *
     * @param autoCommit 提交方式<br>
     *                   (if auto commit)<br>
     * */
    public static synchronized void setAutoCommit(boolean autoCommit)
    {
        DBManager.autoCommit = autoCommit;
    }

    /**
     * 获得一个工作者，用来对数据库进行基本操作<br>
     *
     * (get a worker, and do work!)<br>
     * @return 返回获取到工作者<br>got a DBWorker
     * */
    public static synchronized DBWorker getDBWorker()
    {
        return new DBWorker(getConnection());
    }
}
