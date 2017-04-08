package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Util {

    public static final String EXCHANGE_SUBMISSION_NAME = "submissions";
    public static final String HOST_NAME = "localhost";

    public static final String ADMIN_QUEUE_NAME = "AdminQueue";

    public static final String INJURY_TYPE_ARM = "arm";
    public static final String INJURY_TYPE_ELBOW = "elbow";
    public static final String INJURY_TYPE_KNEE = "knee";

    static Channel createChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Util.HOST_NAME);
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }
}