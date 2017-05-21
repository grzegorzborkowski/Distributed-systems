import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.FindResult;
import model.OrderConfirmation;
import model.OrderResponse;
import model.Request;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSelection serverRef = getContext().actorSelection("../server");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, request -> {
                    switch (request.code) {
                        case FIND:
                            log.info("[Client] Received find request: {}. Passing this message to server", request);
                            serverRef.tell(request, getSelf());
                        case ORDER:
                            log.info("[Client] Received order request: {}. Passing this message to server", request);
                            serverRef.tell(request, getSelf());
                        case STREAM:
                            log.info("[Client] Received stream request: {}. Passing this message to server", request);
                            serverRef.tell(request, getSelf());
                    }
                })
                .match(FindResult.class, result -> {
                    if(result.getPrice() == -1) {
                        log.info("[Client] Book: {} doesn't exist in database", result.getBookName());
                    } else {
                        log.info("[Client] Book: {} cost: {}", result.getBookName(), result.getPrice());
                    }
                })
                .match(OrderResponse.class, orderResponse -> {
                    if(orderResponse.getOrderConfirmation() == OrderConfirmation.SUCCESS) {
                        log.info("[Client] Book : {} order saved successfully", orderResponse.getBookName());
                    } else {
                        log.info("[Client] Book: {} order failed because such book doesn't exist in database", orderResponse.getBookName());
                    }
                })
                .match(String.class, s -> {
                    log.info("[Client] Received : {}", s);
                })
                .matchAny(any -> log.info("[Client] Received unkown message"))
                .build();
    }
}