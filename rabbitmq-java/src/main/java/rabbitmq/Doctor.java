package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Doctor {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Util.HOST_NAME);
        Connection connection;
        connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Util.EXCHANGE_NAME, "topic");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter injury type and surname");
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        String injuryType = parts[0];
        String surname = parts[1];


        channel.basicPublish(Util.EXCHANGE_NAME, injuryType, null, surname.getBytes());
        System.out.println(" [x] Sent '" + injuryType + "':'" + surname + "'");
        connection.close();
    }
}
