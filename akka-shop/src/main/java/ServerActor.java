import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.FindResult;
import model.OrderRequest;
import model.Request;

import java.util.HashMap;
import java.util.Map;


public class ServerActor extends AbstractActor {
    private static final String CSV_FIRST_DB_NAME = "csv/first_db.csv";
    private static final String CSV_SECOND_DB_NAME = "csv/second_db.csv";

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef first_databasefindActor = getContext().getSystem().actorOf(Props.create(DatabaseFinderActor.class, CSV_FIRST_DB_NAME));
    private final ActorRef second_databasefindActor = getContext().getSystem().actorOf(Props.create(DatabaseFinderActor.class, CSV_SECOND_DB_NAME));
    private final ActorRef order_Actor = getContext().getSystem().actorOf(Props.create(OrderActor.class, first_databasefindActor, second_databasefindActor));

    private  ActorRef clientRef;
    private Map<String, Double> receivedFindResults;

    public ServerActor() {
        log.info("Created server successfully : {}", getSelf());
        this.receivedFindResults = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, request -> {
                    if (clientRef == null) {
                        clientRef = getSender();
                        log.info("[Server] Client Ref is: {} ", clientRef);
                    }
                        switch (request.code) {
                            case FIND:
                                    log.info("[Server]: Received FIND request : {}", request);
                                    first_databasefindActor.tell(request.bookName, getSelf());
                                    second_databasefindActor.tell(request.bookName, getSelf());
                                    break;
                            case ORDER:
                                    log.info("[Server]: Received ORDER request: {}", request);
                                    OrderRequest orderRequest = new OrderRequest(getSender(), request.bookName);
                                    order_Actor.tell(orderRequest, getSender());
                                    break;
                            case STREAM:
                                break;
                        }
                    })
                .match(FindResult.class, findResult -> {
                    log.info("[Server]: Received price of a book: {} {}",
                            findResult.getBookName(), findResult.getPrice());
                    if(findResult.getPrice() != -1) {
                        receivedFindResults.put(findResult.getBookName(), findResult.getPrice());
                        clientRef.tell(findResult, null);
                    } else {
                        if(!receivedFindResults.containsKey(findResult.getBookName())) {
                            FindResult result = new FindResult(findResult.getBookName(), -1);
                            clientRef.tell(result, null);
                        }
                    }
                })
                .matchAny(any -> log.info("[Server]: Received unkown message"))
                .build();
    }
}