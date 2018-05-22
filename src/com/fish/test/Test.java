package com.fish.test;

import com.fish.core.DBManager;
import com.fish.core.DBWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fish
 * created by 2018-05-22 21:46
 */
public class Test
{
    public static void main(String[] args)
    {
        DBManager.init(new File("DB.properties"));
        DBWorker dbWorker = DBManager.getDBWorker();

        dbWorker.workBatch(new String[] {
                "INSERT INTO book(name, price) VALUES('论三国', 23), ('孙子兵法', 24);",
                "INSERT INTO book(name, price) VALUE('世界之大宇宙之小', 89);"
        });

        dbWorker.sleep();
    }
}
