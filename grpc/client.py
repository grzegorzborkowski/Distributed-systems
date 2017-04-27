import grpc
import services_pb2_grpc
from services_pb2 import Patient

channel = grpc.insecure_channel('localhost:50051')
stub = services_pb2_grpc.ServiceStub(channel)
patient = Patient(first_name = "Michael", last_name = "Corleone")
examination = stub.GetLastExaminationByPatient(patient)
if examination.id == -1:
    print ("Such patient doesn't exist in database")
else:
    print (examination)