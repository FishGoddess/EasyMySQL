package com.fish.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用于日志输出的工具类
 *
 * (for log...)
 * @author Fish
 * */
public final class Logger
{
    // 格式化时间
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 获取本地时间
    private static String now()
    {
        return sdf.format(new Date());
    }

    /**
     * 输出日志信息
     *
     * @param info 要被输出的日志信息
     * */
    public static void info(String info)
    {
        System.err.println(now() + " [INFO] ==> " + info);
    }

    /**
     * 输出警告信息
     *
     * @param warn 要被输出的警告信息
     */
    public static void warn(String warn)
    {
        System.err.println(now() + " [WARN] ==> " + warn);
    }

    /**
     * 输出错误信息，并终止程序
     *
     * @param err 要被输出的错误信息
     * */
    public static void err(String err)
    {
        System.err.println(now() + " [ERROR] ==> " + err);
        System.exit(1);
    }
}
