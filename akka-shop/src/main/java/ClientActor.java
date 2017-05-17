import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSelection actorRef = getContext().actorSelection("../server");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    log.info("Received string message : {}", s);
                    actorRef.tell("Hello", self());
                })
                .matchAny(any -> log.info("Received unkown message"))
                .build();
    }
}
