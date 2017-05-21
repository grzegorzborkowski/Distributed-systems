import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSelection serverRef = getContext().actorSelection("../server");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if(s.startsWith("find")) {
                        log.info("[Client] Received find order: {}. Passing this message to server", s);
                        serverRef.tell(s, null);
                    } else if(s.startsWith("order")) {

                    } else if(s.startsWith("stream")) {

                    } else {
                        log.info("Received unknown message : {}", s);
                    }

                })
                .matchAny(any -> log.info("Received unkown message"))
                .build();
    }
}