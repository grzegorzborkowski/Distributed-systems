from concurrent import futures
from time import sleep
import grpc
import services_pb2
import services_pb2_grpc
import persistence


class ServiceServicer(services_pb2_grpc.ServiceServicer):

    def __init__(self):
        self.data = persistence

    def GetLastExaminationByPatient(self, request, context):
        patient = ServiceServicer.find_patient_with_given_name(
            self, request.first_name, request.last_name)
        if patient:
            return patient.examinations[-1]
        else:
            return services_pb2.Examination(id=-1)

    def GetAllExaminationByPatient(self, request, context):
        patient = ServiceServicer.find_patient_with_given_name(
            self, request.first_name, request.last_name)
        if patient:
            for examination in patient.examinations:
                yield examination

    def getAllExaminations(self, request, context):
        for patient in self.data.patients:
            for examination in patient.examinations:
                yield examination

    def getAllExaminationWithGivenParameterName(self, request, context):
        for patient in self.data.patients:
            for examination in patient.examinations:
                    for parameter in examination.results.parameters:
                        if parameter.parameter_name.name == request.name:
                                yield examination

    def getAllExaminationWithGivenParameterNameAndRange(self, request, context):
        for patient in self.data.patients:
            for examination in patient.examinations:
                for parameter in examination.results.parameters:
                    if parameter.parameter_name.name == request.parameter_name.name and \
                                            request.lwbound <= parameter.value <= request.upbound:
                        yield examination

    def insertExamination(self, request, context):
        doctor = ServiceServicer.find_doctor_with_given_name(self,
            request.doctor_first_name, request.doctor_last_name)
        if doctor:
            patient = ServiceServicer.find_patient_with_given_name(
                self, request.patient_first_name, request.patient_last_name
            )
            if patient:
                examination = services_pb2.Examination(
                    id=persistence.examination_id,
                    date=request.date,
                    doctor=doctor,
                    results=request.results)
                persistence.insert_examination(examination, patient)
                return services_pb2.StatusMessage(result="Inserting examination succeeded")
            else:
                return services_pb2.StatusMessage(
                    result="Such patient doesnt exist"
                )
        else:
            print("Such doctor doesn't work in this hospital")
            return services_pb2.StatusMessage(result="No such doctor exist")

    @staticmethod
    def find_patient_with_given_name(self, first_name, last_name):
        for patient in self.data.patients:
            if patient.first_name == first_name and patient.last_name == last_name:
                return patient
        return None

    @staticmethod
    def find_doctor_with_given_name(self, first_name, last_name):
        for doctor in self.data.doctors:
            if doctor.first_name == first_name and doctor.last_name == last_name:
                return doctor
        return None

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    services_pb2_grpc.add_ServiceServicer_to_server(
        ServiceServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            sleep(1000000)
    except KeyboardInterrupt:
        server.stop(0)

serve()
