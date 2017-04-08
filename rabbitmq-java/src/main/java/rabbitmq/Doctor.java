package rabbitmq;

import com.rabbitmq.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Doctor {
    private BufferedReader bufferedReader;
    private Channel submissionChannel;
    private Channel responseChannel;

    private Doctor() throws Exception {
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter injury type and surname");
        this.submissionChannel = Util.createChannel();
        this.responseChannel = Util.createChannel();
        submissionChannel.exchangeDeclare(Util.EXCHANGE_SUBMISSION_NAME, "topic");
//        responseChannel.exchangeDeclare(Util.EXCHANGE_RESPONSE_NAME, "direct");
//        AMQP.Queue.DeclareOk result = responseChannel.queueDeclare("knee", false, false, false, null);
//        String queueName = result.getQueue();
//        responseChannel.queueBind("knee", Util.EXCHANGE_RESPONSE_NAME, "knee");
//        responseChannel.basicConsume(queueName, false, this.responseConsumer);
    }

    private PatientDetails readPatientDetails() throws IOException {
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        String injuryType = parts[0];
        String surname = parts[1];
        PatientDetails patientDetails = new PatientDetails(injuryType, surname);
        return patientDetails;
    }

//    private Consumer responseConsumer = new DefaultConsumer(submissionChannel) {
//        @Override
//        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//            String message = new String(body);
//            System.out.println(message);
//        }
//    };

    public static void main(String[] args) throws Exception {
        Doctor doctor = new Doctor();
        while (true) {
            PatientDetails patientDetails = doctor.readPatientDetails();
            doctor.submissionChannel.basicPublish(Util.EXCHANGE_SUBMISSION_NAME, patientDetails.injuryType,
                    null, patientDetails.getMessage().getBytes());
            System.out.println("[x] Sent '" + patientDetails.injuryType + "':'" + patientDetails.surname + "'");
        }
    }
}
