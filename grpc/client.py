import grpc
import services_pb2_grpc

from generated.services_pb2 import Patient

channel = grpc.insecure_channel('localhost:50051')
stub = services_pb2_grpc.ServiceStub(channel)
patient = Patient(first_name = "alan", last_name = "sikora")
examination = stub.GetExaminationByPatient(patient)
print(examination)