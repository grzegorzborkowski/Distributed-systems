import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.opencsv.CSVReader;
import model.FindResult;

import java.io.FileReader;
import java.io.IOException;

public class DatabaseFinderActor extends AbstractActor{
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private String path;
    private CSVReader csvReader;

    public DatabaseFinderActor(String filenamepath) {
        this.path = filenamepath;

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    log.info("[DatabaseFinder:{}]: Received string message : {}", path, s);
                    String bookName = s.split(" ")[1];
                    double bookPrice = getPrice(bookName);
                    FindResult findResult = new FindResult(bookName, bookPrice);
                    this.getSender().tell(findResult, null);
                })
                .matchAny(any -> log.info("[DatabaseFinder]: Received unkown message"))
                .build();
    }

    private double getPrice(String bookName) {
        try {
            this.csvReader = new CSVReader(new FileReader(this.path));
            String[] line;
            while((line=csvReader.readNext()) != null) {
                if(line[0].equals(bookName)) {
                    return Double.parseDouble(line[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

