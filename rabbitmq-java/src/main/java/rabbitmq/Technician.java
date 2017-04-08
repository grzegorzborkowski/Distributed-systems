package rabbitmq;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Technician {

    public static void main(String[] args) throws IOException, TimeoutException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter injury types");
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        String first_injuryType = parts[0];
        String second_injuryType = parts[1];

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Util.HOST_NAME);
        Connection connection = factory.newConnection();
        final Channel submissionChannel = connection.createChannel();

        submissionChannel.exchangeDeclare(Util.EXCHANGE_NAME, "topic");
        AMQP.Queue.DeclareOk first_result = submissionChannel.queueDeclare(first_injuryType, false, false, false, null);
        AMQP.Queue.DeclareOk second_result = submissionChannel.queueDeclare(second_injuryType, false, false, false, null);
        String first_name = first_result.getQueue();
        String second_name = second_result.getQueue();

        submissionChannel.queueBind(first_injuryType, Util.EXCHANGE_NAME, first_injuryType);
        submissionChannel.queueBind(second_injuryType, Util.EXCHANGE_NAME, second_injuryType);

        Consumer consumer = new DefaultConsumer(submissionChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                String injuryType = message.split(" ")[0];
                String surname = message.split(" ")[1];
                System.out.println("Received injury " + injuryType + " surname:" + surname);
            }
        };

        submissionChannel.basicConsume(first_name, false, consumer);
        submissionChannel.basicConsume(second_name, false, consumer);
    }
}