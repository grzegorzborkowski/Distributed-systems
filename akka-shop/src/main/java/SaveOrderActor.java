import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.OrderConfirmation;
import model.OrderRequest;
import model.OrderResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SaveOrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ActorRef sender;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, orderRequest -> {
                    log.info("[SaveOrderActor] Received order: {}", orderRequest);
                    this.sender = getSender();
                    makeOrder(orderRequest);
                })
                .matchAny(any -> log.info("[SaveOrderActor] Unknown message received"))
                .build();
    }

    private void makeOrder(String bookName) {
            try {
                Files.write(Paths.get("orders.txt"), bookName.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        OrderResponse orderResponse = new OrderResponse(bookName, OrderConfirmation.SUCCESS);
        this.sender.tell(orderResponse, null);
    }

}
