import grpc
import services_pb2_grpc
from services_pb2 import Patient, Empty, ParameterName, ParameterRange

channel = grpc.insecure_channel('localhost:50051')
stub = services_pb2_grpc.ServiceStub(channel)

# patient = Patient(first_name = "John", last_name = "Kowalsky")
# for examination in stub.GetAllExaminationByPatient(patient):
#     print(examination)

# examination = stub.GetLastExaminationByPatient(patient)
# if examination.id == -1:
#     print ("Such patient doesn't exist in database")
# else:
#     print (examination)
# ###
# for examination in stub.getAllExaminations(Empty()):
#     print (examination)
#
# for examination in stub.getAllExaminationWithGivenParameterName(
#     ParameterName(
#     name="RedBloodCells")):
#     print (examination)

for res in stub.getAllExaminationWithGivenParameterNameAndRange(
    ParameterRange(
        parameter_name=ParameterName(name="RedBloodCells"), lwbound=40, upbound=100)):
    print(res)