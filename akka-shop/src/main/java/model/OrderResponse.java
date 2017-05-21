package model;

public class OrderResponse {
    String bookName;
    OrderConfirmation orderConfirmation;

    public OrderResponse(String bookName, OrderConfirmation orderConfirmation) {
        this.bookName = bookName;
        this.orderConfirmation = orderConfirmation;
    }

    public String getBookName() {
        return bookName;
    }

    public OrderConfirmation getOrderConfirmation() {
        return orderConfirmation;
    }
}
