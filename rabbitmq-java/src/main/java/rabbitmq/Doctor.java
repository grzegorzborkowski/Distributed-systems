package rabbitmq;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Doctor {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Util.HOST_NAME);
        Connection connection;
        connection = factory.newConnection();
        Channel submissionChannel = connection.createChannel();
        submissionChannel.exchangeDeclare(Util.EXCHANGE_NAME, "topic");
        Channel resultChannel = connection.createChannel();
        resultChannel.exchangeDeclare(Util.EXCHANGE_NAME, "topic");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter injury type and surname");
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        String injuryType = parts[0];
        String surname = parts[1];
        submissionChannel.basicPublish(Util.EXCHANGE_NAME, injuryType, null, message.getBytes());
        System.out.println(" [x] Sent '" + injuryType + "':'" + surname + "'");

        AMQP.Queue.DeclareOk knee = submissionChannel.queueDeclare("knee_response", false, false, false, null);
        AMQP.Queue.DeclareOk arm = submissionChannel.queueDeclare("arm_response", false, false, false, null);
        String knee_queueName = knee.getQueue();
        String arm_queueName = arm.getQueue();
        System.out.println(knee_queueName);
        System.out.println(arm_queueName);


        Consumer consumer = new DefaultConsumer(resultChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println("Received following response: "+ message);
            }
        };

        resultChannel.basicConsume(knee_queueName, consumer);
        resultChannel.basicConsume(arm_queueName, consumer);
    }
}
