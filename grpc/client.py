import grpc


import services_pb2_grpc
from services_pb2 import Patient, Empty, ParameterName, ParameterRange, Examination_Request, Results, Parameter

# TODO: rewrite client to node.js
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
#
for res in stub.getAllExaminationWithGivenParameterNameAndRange(
    ParameterRange(
        parameter_name=ParameterName(name="RedBloodCells"), lwbound=40, upbound=100)):
    print(res)

# patient = Patient(first_name = "John", last_name = "Kowalsky")
# for examination in stub.getAllExaminationByPatient(patient):
#     print(examination)
# res = stub.insertExamination(Examination_Request(
#     doctor_first_name="Sigmund", doctor_last_name="Freud",
#     patient_first_name="John", patient_last_name="Kowalsky",
#     results=Results(
#         parameters=[
#             Parameter(parameter_name=ParameterName(name="BloodPressure"),
#                       value=300)])))
# print(res)
#
# patient = Patient(first_name = "John", last_name = "Kowalsky")
# for examination in stub.getAllExaminationByPatient(patient):
#     print(examination)

