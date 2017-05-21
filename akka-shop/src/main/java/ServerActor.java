import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import model.FindResult;
import model.OrderRequest;
import model.Request;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ServerActor extends AbstractActor {
    private static final String CSV_FIRST_DB_NAME = "csv/first_db.csv";
    private static final String CSV_SECOND_DB_NAME = "csv/second_db.csv";
    private static final String STREAM_BOOK_FILENAME = "books/example_book.txt";

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef first_databasefindActor = getContext().getSystem().actorOf(Props.create(DatabaseFinderActor.class, CSV_FIRST_DB_NAME));
    private final ActorRef second_databasefindActor = getContext().getSystem().actorOf(Props.create(DatabaseFinderActor.class, CSV_SECOND_DB_NAME));
    private final ActorRef order_Actor = getContext().getSystem().actorOf(Props.create(OrderActor.class));
    private final Materializer materalizer = ActorMaterializer.create(getContext().getSystem());

    private ActorRef clientRef;
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
                                log.info("[Server]: Received STREAM request. {} Starting streaming to Client ...", request);
                                List<String> lineList = Files.readAllLines(new File(STREAM_BOOK_FILENAME).toPath(),
                                        Charset.defaultCharset() );
                                final Source<String, NotUsed> source = Source.from(lineList).
                                throttle(1, Duration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping());
                                sendStream(source, getSender());
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

    private void sendStream(Source<String, NotUsed> source, ActorRef sender) {
        source.runForeach(i -> sender.tell(i, getSelf()), materalizer);
    }
}