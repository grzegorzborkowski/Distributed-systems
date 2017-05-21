package model;

import java.io.Serializable;

public class FindResult implements Serializable {
    private String bookName;
    private String sender;
    private double price;

    public FindResult(String bookName, double price, String sender) {
        this.bookName = bookName;
        this.price = price;
        this.sender = sender;
    }

    public String getBookName() {
        return bookName;
    }

    public double getPrice() {
        return price;
    }

    public String getSender() {
        return sender;
    }
}
