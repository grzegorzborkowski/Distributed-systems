import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.FindResult;
import model.OrderConfirmation;
import model.OrderRequest;
import model.OrderResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

// TODO: fix error when book is saved twice if it exists in both databases
public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ActorRef firstDB, secondDB;
    int receivedResponse = 0;
    private boolean bookSaved = false;
    ActorRef client;

    public OrderActor(ActorRef firstDB, ActorRef secondDB) {
        this.firstDB = firstDB;
        this.secondDB = secondDB;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequest.class, orderRequest -> {
                            log.info("Received order: {}", orderRequest.getBookName());
                            this.firstDB.tell(orderRequest.getBookName(), getSelf());
                            this.secondDB.tell(orderRequest.getBookName(), getSelf());
                            this.client = getSender();
                })
                .match(FindResult.class, findResult -> {
                    System.out.println("receivedResponse, price, bookSaved state" + receivedResponse + findResult.getPrice() + bookSaved);
                    receivedResponse += 1;
                    if(findResult.getPrice() != -1 && !bookSaved) {
                        makeOrder(findResult.getBookName() + "\n");
                        this.bookSaved = true;
                        OrderResponse orderResponse = new OrderResponse(findResult.getBookName(), OrderConfirmation.SUCCESS);
                        sendResponseToClient(orderResponse);
                    }
                    else if(findResult.getPrice() == -1 && receivedResponse==2 && !this.bookSaved) {
                        OrderResponse orderResponse = new OrderResponse(findResult.getBookName(), OrderConfirmation.FAILURE);
                        sendResponseToClient(orderResponse);
                    }
                })
                .matchAny(any -> log.info("[Order] Unknown message received"))
                .build();
    }

    private void sendResponseToClient(OrderResponse orderResponse) {
        this.bookSaved = false;
        this.receivedResponse = 0;
        this.client.tell(orderResponse, getSelf());
    }

    private void makeOrder(String bookName) {
        if (!bookSaved) {
            try {
                Files.write(Paths.get("orders.txt"), bookName.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
