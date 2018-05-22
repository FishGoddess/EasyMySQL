package com.fish.test;

import annotations.Table;

/**
 * @author Fish
 * created by 2018-05-22 21:45
 */
@Table("book")
public class Book
{
    private String name = null;
    private Integer price = null;

    public Book(String name, Integer price)
    {
        this.name = name;
        this.price = price;
    }

    public Book()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getPrice()
    {
        return price;
    }

    public void setPrice(Integer price)
    {
        this.price = price;
    }
}
