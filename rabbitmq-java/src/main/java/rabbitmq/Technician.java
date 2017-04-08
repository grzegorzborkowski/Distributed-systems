package rabbitmq;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Technician {
    public static final int MAX_EXAMINATION_TIME = 5000;
    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private String firstInjuryType = "";
    private String secondInjuryType = "";
    private Channel submissionChannel;
    private Channel responseChannel;
    private Random random;

    public Technician() throws IOException, TimeoutException {
        readInuriesTypes();
        this.submissionChannel = Util.createChannel();
        this.submissionChannel.exchangeDeclare(Util.EXCHANGE_SUBMISSION_NAME, "topic");
        this.responseChannel = Util.createChannel();
        this.bindQueues(firstInjuryType);
        this.bindQueues(secondInjuryType);
        this.random = new Random();
    }

    private void readInuriesTypes() throws IOException {
        System.out.println("Please enter injury types");
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        firstInjuryType = parts[0];
        secondInjuryType = parts[1];
    }

    private void bindQueues(String injuryType) throws IOException {
        AMQP.Queue.DeclareOk result = submissionChannel.queueDeclare(injuryType, false, false, false , null);
        String queueName = result.getQueue();
        submissionChannel.queueBind(injuryType, Util.EXCHANGE_SUBMISSION_NAME, injuryType);
        submissionChannel.basicConsume(queueName, false, this.consumer);
    }

    private Consumer consumer = new DefaultConsumer(submissionChannel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope,
                                   AMQP.BasicProperties properties, byte[] body) throws IOException {
            String message = new String(body);
            String[] message_split = message.split(" ");
            String queueName = message_split[0];
            String surname = message_split[2];
            System.out.println("Otrzymano do badania:" + surname);
            submissionChannel.basicAck(envelope.getDeliveryTag(), false);
            try {
                Thread.sleep(random.nextInt(MAX_EXAMINATION_TIME));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String response = "Otrzymano rezultat badania: " + surname;
            responseChannel.basicPublish("", queueName, null, response.getBytes());
        }
    };

    public static void main(String[] args) throws IOException, TimeoutException {
        new Technician();
    }
}