package model;

import akka.actor.ActorRef;

public class OrderRequest {
    private ActorRef clientRef;
    private String bookName;

    public OrderRequest(ActorRef clientRef, String bookName) {
        this.clientRef = clientRef;
        this.bookName = bookName;
    }

    public ActorRef getClientRef() {
        return clientRef;
    }

    public String getBookName() {
        return bookName;
    }
}
