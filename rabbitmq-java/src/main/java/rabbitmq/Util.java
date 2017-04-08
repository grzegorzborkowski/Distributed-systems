package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Util {

    public static final String EXCHANGE_NAME = "submissions";
    public static final String HOST_NAME = "localhost";

    static Channel createSubmissionChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Util.HOST_NAME);
        Connection connection = factory.newConnection();
        return connection.createChannel();

    }
}