package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Administrator {
    private Connection connection;
    private Channel channel;
    private List<String> logReport;

    private Administrator() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Util.HOST_NAME);
        this.connection = connectionFactory.newConnection();
        this.channel = connection.createChannel();
        this.channel.queueDeclare(Util.ADMIN_QUEUE_NAME, false, false, false, null);
        this.logReport = new ArrayList<>();
    }

    private Consumer administratorConsumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String consumerTag,
                                   Envelope envelope, AMQP.BasicProperties properties,
                                   byte[] body) throws IOException {
            String message = new String(body, "UTF-8");
            if(message.startsWith("getlog")) {
                channel.basicPublish("", Util.ADMIN_QUEUE_NAME, null,
                        logReport.toString().getBytes());
                logReport.add("getlog");
            } else {
                System.out.println("Received: " + message);
                logReport.add(message);
            }
        }};

    public static void main(String[] args) throws IOException, TimeoutException {
        Administrator administrator = new Administrator();
        administrator.channel.
                basicConsume(Util.ADMIN_QUEUE_NAME, false, administrator.administratorConsumer);

    }
}
