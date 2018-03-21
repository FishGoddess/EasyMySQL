

// test class

import annotations.Column;
import annotations.Table;

/**
 * book 实体类
 * @author Fish
 * */
@Table("book")
public class Book
{
    @Column("id")
    private Integer id = null;

    @Column("name")
    private String name = null;

    @Column("price")
    private Integer price = null;

    public Book()
    {}

    public Book(String name, Integer price)
    {
        this.name = name;
        this.price = price;
    }

    public Book(Integer id, String name, Integer price)
    {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
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

    @Override
    public String toString()
    {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}

