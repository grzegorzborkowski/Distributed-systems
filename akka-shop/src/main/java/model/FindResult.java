package model;

import java.io.Serializable;

public class FindResult implements Serializable {
    private String bookName;
    private double price;

    public FindResult(String bookName, double price) {
        this.bookName = bookName;
        this.price = price;
    }

    public String getBookName() {
        return bookName;
    }

    public double getPrice() {
        return price;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
