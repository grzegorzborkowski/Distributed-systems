package rabbitmq;

import com.rabbitmq.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Doctor {
    private BufferedReader bufferedReader;
    private Channel submissionChannel;

    private Doctor() throws Exception {
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter injury type and surname");
        this.submissionChannel = Util.createSubmissionChannel();
        submissionChannel.exchangeDeclare(Util.EXCHANGE_NAME, "topic");
    }

    private PatientDetails readPatientDetails() throws IOException {
        String message = bufferedReader.readLine();
        String[] parts = message.split(" ");
        String injuryType = parts[0];
        String surname = parts[1];
        PatientDetails patientDetails = new PatientDetails(injuryType, surname);
        return patientDetails;
    }

    public static void main(String[] args) throws Exception {
        Doctor doctor = new Doctor();
        while (true) {
            PatientDetails patientDetails = doctor.readPatientDetails();
            doctor.submissionChannel.basicPublish(Util.EXCHANGE_NAME, patientDetails.injuryType,
                    null, patientDetails.getMessage().getBytes());
            System.out.println(" [x] Sent '" + patientDetails.injuryType + "':'" + patientDetails.surname + "'");
        }
    }
}
