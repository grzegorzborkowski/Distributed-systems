import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.FindResult;

import java.util.ArrayList;

public class ServerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef databaseFindActor = getContext().getSystem().actorOf(Props.create(DatabaseFinderActor.class, "csv/first_db.csv"));
//    private ArrayList<FindResult> findResults = new ArrayList<>();
    public ServerActor() {
        log.info(String.valueOf(getSelf()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    log.info("[Server]: Received string message : {}", s);
                    databaseFindActor.tell(s, getSelf());
                })
                .match(FindResult.class, findResult -> {
                    log.info("[Server]: Received price of a book: {} {} from {}", findResult.getBookName(), findResult.getPrice(), findResult.getSender());

                })
                .matchAny(any -> log.info("[Server]: Received unkown message"))
                .build();
    }
}