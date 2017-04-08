package rabbitmq;

import com.rabbitmq.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Doctor {
    private BufferedReader bufferedReader;
    private Channel submissionChannel;
    private Channel responseChannel;
    private String armResponseQueue;
    private String kneeResponseQueue;
    private String elbowResponseQueue;


    private Doctor() throws Exception {
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter injury type and surname");
        this.submissionChannel = Util.createChannel();
        this.responseChannel = Util.createChannel();
        armResponseQueue = this.responseChannel.queueDeclare().getQueue();
        kneeResponseQueue = this.responseChannel.queueDeclare().getQueue();
        elbowResponseQueue = this.responseChannel.queueDeclare().getQueue();

        submissionChannel.exchangeDeclare(Util.EXCHANGE_SUBMISSION_NAME, "topic");
        responseChannel.basicConsume(armResponseQueue, false, responseConsumer);
        responseChannel.basicConsume(kneeResponseQueue,  false, responseConsumer);
        responseChannel.basicConsume(elbowResponseQueue, false, responseConsumer);
    }

    private PatientDetails readPatientDetails() throws IOException {
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        String injuryType = parts[0];
        String surname = parts[1];
        PatientDetails patientDetails = new PatientDetails(injuryType, surname);
        return patientDetails;
    }

    private Consumer responseConsumer = new DefaultConsumer(submissionChannel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope,
                                   AMQP.BasicProperties properties, byte[] body) throws IOException {
            String message = new String(body);
            System.out.println(message);
        }
    };

    public static void main(String[] args) throws Exception {
        Doctor doctor = new Doctor();
        while (true) {
            PatientDetails patientDetails = doctor.readPatientDetails();
            String Message;
            switch (patientDetails.injuryType) {
                case Util.INJURY_TYPE_ARM:
                    Message = doctor.armResponseQueue;
                    break;
                case Util.INJURY_TYPE_ELBOW:
                    Message = doctor.kneeResponseQueue;
                    break;
                case Util.INJURY_TYPE_KNEE:
                    Message = doctor.elbowResponseQueue;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid injury type! Go to another doctor please");
            }
            Message = Message + patientDetails.getMessage();
            doctor.submissionChannel.basicPublish(Util.EXCHANGE_SUBMISSION_NAME, patientDetails.injuryType,
                    null, Message.getBytes());
        }
    }
}
