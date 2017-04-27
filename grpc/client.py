import grpc
import services_pb2_grpc
from services_pb2 import Patient, Empty

channel = grpc.insecure_channel('localhost:50051')
stub = services_pb2_grpc.ServiceStub(channel)

patient = Patient(first_name = "John", last_name = "Kowalsky")
examination = stub.GetLastExaminationByPatient(patient)
if examination.id == -1:
    print ("Such patient doesn't exist in database")
else:
    print (examination)
###
for examination in stub.getAllExaminations(Empty()):
    print (examination)

