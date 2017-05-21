import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.OrderRequest;
import model.OrderResponse;



public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ActorRef client;
    ActorRef saveOrderActor = getContext().getSystem().actorOf(Props.create(SaveOrderActor.class));

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequest.class, orderRequest -> {
                            log.info("[OrderActor] Received order: {}", orderRequest.getBookName());
                            this.client = getSender();
                            this.saveOrderActor.tell(orderRequest.getBookName(), getSelf());
                })
                .match(OrderResponse.class, orderResponse ->{
                    this.client.tell(orderResponse, null);
                })
                .matchAny(any -> log.info("[Order] Unknown message received"))
                .build();
    }
}
