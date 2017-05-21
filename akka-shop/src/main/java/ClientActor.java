import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.FindResult;
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
                            log.info("[Client] Received find order: {}. Passing this message to server", request);
                            serverRef.tell(request, getSelf());
                        case ORDER:
                            break;
                        case STREAM:
                            break;
                    }
                })
                .match(FindResult.class, result -> {
                    if(result.getPrice() == -1) {
                        log.info("[Client] Book: {} doesn't exist in database", result.getBookName());
                    } else {
                        log.info("[Client] Book: {} cost: {}", result.getBookName(), result.getPrice());
                    }
                })
                .matchAny(any -> log.info("[Client] Received unkown message"))
                .build();
    }
}