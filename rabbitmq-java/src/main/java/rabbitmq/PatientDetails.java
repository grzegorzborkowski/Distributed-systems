package rabbitmq;

public class PatientDetails {
    String injuryType;
    String surname;

    public PatientDetails(String injuryType, String surname) {
        this.injuryType = injuryType;
        this.surname = surname;
    }

    public String getMessage() {
        return injuryType + " " + surname;
    }
}
