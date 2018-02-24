package com.fish.EasyMysql;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 管理 MySql 数据库的连接信息
 * some infomation about database's connection
 * @author Fish
 * */
public final class DBManager
{
    /**
     * 数据库连接配置类 config class
     * 配置文件
     * 一开始就把数据库连接配置文件加载到 config 文件中，提高效率
     * @author Fish
     * */
    private static class Config
    {
        private Config()
        {}

        /**
         * 数据库连接驱动名字
         */
        private static final String DRIVER = "com.mysql.jdbc.Driver";

        /**
         * 数据库连接主机
         */
        private static String databaseHost = null;

        /**
         * 用户名
         */
        private static String user = null;

        /**
         * 密码
         */
        private static String password = null;

        /**
         * 数据库连接地址
         */
        private static StringBuilder url = null;

        /**
         * 正在使用的数据库
         * */
        private static String usingDatabase = null;
    }

    /**
     * 标记是否已经初始化，除非调用 update 方法，否则该变量不应该被改写
     * mark the status of initation
     * */
    private static boolean isInited = false;

    /**
     * 配置文件所在路径
     * where the config file hides?
     * */
    private static File filePath = null;

    /**
     * 不需要存在这个类的对象，只需要调用所需的方法即可
     * single dog, single dog, single all the way! Which song you are reminded of?
     * */
    private DBManager()
    {}

    /**
     * 初始化数据库管理器
     * init this guy
     * @param path 指定配置文件的路径 the home of config file
     * @return true 初始化成功 good job!， false 初始化失败 you failed!
     * */
    private static boolean init(File path)
    {
        if (!isInited)
        {
            filePath = path;
            Config.url = new StringBuilder();

            try
            {
                Properties properties = new Properties();
                properties.load(new BufferedInputStream(new FileInputStream(filePath)));

                Config.databaseHost = properties.getProperty("databaseHost");
                Config.user = properties.getProperty("user");
                Config.password = properties.getProperty("password");
                Config.usingDatabase = properties.getProperty("usingDatabase");

                Config.url.append("jdbc:mysql://");
                Config.url.append(Config.databaseHost + "/");
                Config.url.append(Config.usingDatabase + "?useSSL=false");

                isInited = true;
            }
            catch (IOException e)
            {
                System.out.println("配置文件读取异常");
                return false;
            }
        }

        return true;
    }

    /**
     * 根据指定文件更新数据库管理器的配置信息
     * update connection depending on what config you give
     * 在调用该方法之前，建议先调用 DBWorker.sleep() 释放资源！
     * Maybe you should invoke DBWorker.sleep() before invoking this method!
     * @param path 指定配置文件的路径
     * @return true 更新成功， false 更新失败
     * */
    public static boolean update(File path)
    {
        isInited = false;
        filePath = path;

        return init(filePath);
    }

    /**
     * 更新数据库管理器的配置信息
     * update connection using the old config file
     * 按照第一次初始化的路径进行更新
     * 在调用该方法之前，建议先调用 DBWorker.sleep() 释放资源！
     * Maybe you should invoke DBWorker.sleep() before invoking this method!
     * @return true 更新成功， false 更新失败
     */
    public static boolean update()
    {
        isInited = false;

        return init(filePath);
    }

    /**
     * 获取数据库连接, 不推荐使用
     * get a connection of database, don't advise!
     * @return 返回得到的数据库连接 you get a connection
     * */
    public static Connection getConnection()
    {
        // 初始化管理器
        init(new File("DB.properties"));

        try
        {
            // 加载数据库连接驱动
            Class.forName(Config.DRIVER);
            return DriverManager.getConnection(Config.url.toString(), Config.user, Config.password);
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
        }
    }
}
