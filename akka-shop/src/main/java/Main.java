import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import model.Request;
import model.RequestCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        ActorSystem actorSystem = ActorSystem.create("BookShop");
        actorSystem.actorOf(Props.create(ServerActor.class), "server");
        ActorRef client = actorSystem.actorOf(Props.create(ClientActor.class), "client");
        System.out.println("Menu: find [name], order [name], stream , q");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            Request request = null;
            String line = br.readLine();
            String[] split = line.split(" ");
            if (line.equals("q")) {
                break;
            }
            if(split[0].startsWith("find")) {
                request = new Request(RequestCode.FIND, split[1]);
            } else if(split[0].startsWith("order")) {
                request = new Request(RequestCode.ORDER, split[1]);
            } else if(split[0].startsWith("stream")) {
                request = new Request(RequestCode.STREAM, null);
            } else {
                System.out.println("Unknown command!");
            }

            client.tell(request, null);
        }

        actorSystem.terminate();
    }
}