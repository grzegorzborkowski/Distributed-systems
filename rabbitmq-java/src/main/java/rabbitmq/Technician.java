package rabbitmq;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Technician {

    public static void main(String[] args) throws IOException, TimeoutException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter injury type");
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        String injuryType = parts[0];

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Util.HOST_NAME);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Util.EXCHANGE_NAME, "topic");
        AMQP.Queue.DeclareOk result = channel.queueDeclare(injuryType, false, false, false, null);
        String name = result.getQueue();

        channel.queueBind(injuryType, Util.EXCHANGE_NAME, injuryType);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println("Received message: " + message);
            }
        };

        channel.basicConsume(name, false, consumer);
    }
}
