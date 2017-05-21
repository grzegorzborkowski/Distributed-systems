import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.FindResult;

import java.util.HashMap;
import java.util.Map;

public class ServerActor extends AbstractActor {
    private static final String CSV_FIRST_DB_NAME = "csv/first_db.csv";
    private static final String CSV_SECOND_DB_NAME = "csv/second_db.csv";

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef first_databasefindActor = getContext().getSystem().actorOf(Props.create(DatabaseFinderActor.class, CSV_FIRST_DB_NAME));
    private final ActorRef second_databasefindActor = getContext().getSystem().actorOf(Props.create(DatabaseFinderActor.class, CSV_SECOND_DB_NAME));
    private  ActorRef clientRef;
    private Map<String, Double> receivedFindResults;

    public ServerActor() {
        log.info("Created server successfully : {}", getSelf());
        this.receivedFindResults = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if(clientRef == null) {
                        clientRef = getSender();
                    }
                    log.info("[Server] Client Ref is: {} ", clientRef);
                    log.info("[Server]: Received string message : {}", s);
                    first_databasefindActor.tell(s, getSelf());
                    second_databasefindActor.tell(s, getSelf());
                })
                .match(FindResult.class, findResult -> {
                    log.info("[Server]: Received price of a book: {} {}",
                            findResult.getBookName(), findResult.getPrice());
                    if(findResult.getPrice() != -1) {
                        receivedFindResults.put(findResult.getBookName(), findResult.getPrice());
                        clientRef.tell("result " + findResult.getBookName() + " " + findResult.getPrice(), null);
                    } else {
                        if(!receivedFindResults.containsKey(findResult.getBookName())) {
                            clientRef.tell("result " + findResult.getBookName() + " doesn't exist in database", null);
                        }
                    }
                })
                .matchAny(any -> log.info("[Server]: Received unkown message"))
                .build();
    }
}