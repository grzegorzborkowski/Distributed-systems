import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ServerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public ServerActor() {
        log.info(String.valueOf(getSelf()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    log.info("[Server]: Received string message : {}", s);
                })
                .matchAny(any -> log.info("[Server]: Received unkown message"))
                .build();
    }
}
