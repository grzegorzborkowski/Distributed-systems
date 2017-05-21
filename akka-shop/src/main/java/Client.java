import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) throws IOException {
        ActorSystem actorSystem = ActorSystem.create("BookShop");
        actorSystem.actorOf(Props.create(ServerActor.class), "server");
        ActorRef client = actorSystem.actorOf(Props.create(ClientActor.class), "client");
        System.out.println("Menu: find [name], order [name], stream[name], q");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            client.tell(line, null);
        }

        actorSystem.terminate();
    }
}